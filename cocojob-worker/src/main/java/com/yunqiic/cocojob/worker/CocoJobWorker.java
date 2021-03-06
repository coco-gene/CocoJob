package com.yunqiic.cocojob.worker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import com.yunqiic.cocojob.common.exception.CocoJobException;
import com.yunqiic.cocojob.common.RemoteConstant;
import com.yunqiic.cocojob.common.response.ResultDTO;
import com.yunqiic.cocojob.common.utils.CommonUtils;
import com.yunqiic.cocojob.common.utils.HttpUtils;
import com.yunqiic.cocojob.common.serialize.JsonUtils;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.worker.actors.ProcessorTrackerActor;
import com.yunqiic.cocojob.worker.actors.TaskTrackerActor;
import com.yunqiic.cocojob.worker.actors.TroubleshootingActor;
import com.yunqiic.cocojob.worker.actors.WorkerActor;
import com.yunqiic.cocojob.worker.background.OmsLogHandler;
import com.yunqiic.cocojob.worker.background.ServerDiscoveryService;
import com.yunqiic.cocojob.worker.background.WorkerHealthReporter;
import com.yunqiic.cocojob.worker.common.CocoJobWorkerConfig;
import com.yunqiic.cocojob.worker.common.PowerBannerPrinter;
import com.yunqiic.cocojob.worker.common.WorkerRuntime;
import com.yunqiic.cocojob.worker.common.utils.SpringUtils;
import com.yunqiic.cocojob.worker.persistence.TaskPersistenceService;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ??????????????????
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
public class CocoJobWorker implements ApplicationContextAware, InitializingBean, DisposableBean {

    private ScheduledExecutorService timingPool;
    private final WorkerRuntime workerRuntime = new WorkerRuntime();
    private final AtomicBoolean initialized = new AtomicBoolean();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.inject(applicationContext);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void init() throws Exception {

        if (!initialized.compareAndSet(false, true)) {
            log.warn("[CocoJobWorker] please do not repeat the initialization");
            return;
        }

        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("[CocoJobWorker] start to initialize CocoJobWorker...");

        CocoJobWorkerConfig config = workerRuntime.getWorkerConfig();
        CommonUtils.requireNonNull(config, "can't find OhMyConfig, please set OhMyConfig first");

        try {
            PowerBannerPrinter.print();
            // ?????? appName
            if (!config.isEnableTestMode()) {
                assertAppName();
            }else {
                log.warn("[CocoJobWorker] using TestMode now, it's dangerous if this is production env.");
            }

            // ??????????????????
            String workerAddress = NetUtils.getLocalHost() + ":" + config.getPort();
            workerRuntime.setWorkerAddress(workerAddress);

            // ????????????????????????
            ThreadFactory timingPoolFactory = new ThreadFactoryBuilder().setNameFormat("oms-worker-timing-pool-%d").build();
            timingPool = Executors.newScheduledThreadPool(3, timingPoolFactory);

            // ?????? server
            ServerDiscoveryService serverDiscoveryService = new ServerDiscoveryService(workerRuntime.getAppId(), workerRuntime.getWorkerConfig());
            serverDiscoveryService.start(timingPool);
            workerRuntime.setServerDiscoveryService(serverDiscoveryService);

            // ????????? ActorSystem???macOS??? new ServerSocket ???????????????????????????????????????????????????AKKA???Scala????????????????????????...???????????????????????????
            Map<String, Object> overrideConfig = Maps.newHashMap();
            overrideConfig.put("akka.remote.artery.canonical.hostname", NetUtils.getLocalHost());
            overrideConfig.put("akka.remote.artery.canonical.port", config.getPort());

            Config akkaBasicConfig = ConfigFactory.load(RemoteConstant.WORKER_AKKA_CONFIG_NAME);
            Config akkaFinalConfig = ConfigFactory.parseMap(overrideConfig).withFallback(akkaBasicConfig);

            int cores = Runtime.getRuntime().availableProcessors();
            ActorSystem actorSystem = ActorSystem.create(RemoteConstant.WORKER_ACTOR_SYSTEM_NAME, akkaFinalConfig);
            workerRuntime.setActorSystem(actorSystem);

            ActorRef taskTrackerActorRef = actorSystem.actorOf(TaskTrackerActor.props(workerRuntime)
                    .withDispatcher("akka.task-tracker-dispatcher")
                    .withRouter(new RoundRobinPool(cores * 2)), RemoteConstant.TASK_TRACKER_ACTOR_NAME);
            actorSystem.actorOf(ProcessorTrackerActor.props(workerRuntime)
                    .withDispatcher("akka.processor-tracker-dispatcher")
                    .withRouter(new RoundRobinPool(cores)), RemoteConstant.PROCESSOR_TRACKER_ACTOR_NAME);
            actorSystem.actorOf(WorkerActor.props(taskTrackerActorRef)
                    .withDispatcher("akka.worker-common-dispatcher")
                    .withRouter(new RoundRobinPool(cores)), RemoteConstant.WORKER_ACTOR_NAME);

            // ????????????????????????????????????
            ActorRef troubleshootingActor = actorSystem.actorOf(Props.create(TroubleshootingActor.class), RemoteConstant.TROUBLESHOOTING_ACTOR_NAME);
            actorSystem.eventStream().subscribe(troubleshootingActor, DeadLetter.class);

            log.info("[CocoJobWorker] akka-remote listening address: {}", workerAddress);
            log.info("[CocoJobWorker] akka ActorSystem({}) initialized successfully.", actorSystem);

            // ?????????????????????
            OmsLogHandler omsLogHandler = new OmsLogHandler(workerAddress, actorSystem, serverDiscoveryService);
            workerRuntime.setOmsLogHandler(omsLogHandler);

            // ???????????????
            TaskPersistenceService taskPersistenceService = new TaskPersistenceService(workerRuntime.getWorkerConfig().getStoreStrategy());
            taskPersistenceService.init();
            workerRuntime.setTaskPersistenceService(taskPersistenceService);
            log.info("[CocoJobWorker] local storage initialized successfully.");

            // ?????????????????????
            timingPool.scheduleAtFixedRate(new WorkerHealthReporter(workerRuntime), 0, 15, TimeUnit.SECONDS);
            timingPool.scheduleWithFixedDelay(omsLogHandler.logSubmitter, 0, 5, TimeUnit.SECONDS);

            log.info("[CocoJobWorker] CocoJobWorker initialized successfully, using time: {}, congratulations!", stopwatch);
        }catch (Exception e) {
            log.error("[CocoJobWorker] initialize CocoJobWorker failed, using {}.", stopwatch, e);
            throw e;
        }
    }

    public void setConfig(CocoJobWorkerConfig config) {
        workerRuntime.setWorkerConfig(config);
    }

    @SuppressWarnings("rawtypes")
    private void assertAppName() {

        CocoJobWorkerConfig config = workerRuntime.getWorkerConfig();
        String appName = config.getAppName();
        Objects.requireNonNull(appName, "appName can't be empty!");

        String url = "http://%s/server/assert?appName=%s";
        for (String server : config.getServerAddress()) {
            String realUrl = String.format(url, server, appName);
            try {
                String resultDTOStr = CommonUtils.executeWithRetry0(() -> HttpUtils.get(realUrl));
                ResultDTO resultDTO = JsonUtils.parseObject(resultDTOStr, ResultDTO.class);
                if (resultDTO.isSuccess()) {
                    Long appId = Long.valueOf(resultDTO.getData().toString());
                    log.info("[CocoJobWorker] assert appName({}) succeed, the appId for this application is {}.", appName, appId);
                    workerRuntime.setAppId(appId);
                    return;
                }else {
                    log.error("[CocoJobWorker] assert appName failed, this appName is invalid, please register the appName {} first.", appName);
                    throw new CocoJobException(resultDTO.getMessage());
                }
            }catch (CocoJobException oe) {
                throw oe;
            }catch (Exception ignore) {
                log.warn("[CocoJobWorker] assert appName by url({}) failed, please check the server address.", realUrl);
            }
        }
        log.error("[CocoJobWorker] no available server in {}.", config.getServerAddress());
        throw new CocoJobException("no server available!");
    }

    @Override
    public void destroy() throws Exception {
        timingPool.shutdownNow();
        workerRuntime.getActorSystem().terminate();
    }
}
