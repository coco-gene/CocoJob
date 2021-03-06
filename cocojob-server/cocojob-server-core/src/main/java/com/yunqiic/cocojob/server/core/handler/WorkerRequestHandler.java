package com.yunqiic.cocojob.server.core.handler;

import akka.actor.Props;
import akka.routing.RoundRobinPool;
import com.yunqiic.cocojob.common.enums.InstanceStatus;
import com.yunqiic.cocojob.common.RemoteConstant;
import com.yunqiic.cocojob.common.request.*;
import com.yunqiic.cocojob.server.core.handler.impl.WorkerRequestAkkaHandler;
import com.yunqiic.cocojob.server.core.handler.impl.WorkerRequestHttpHandler;
import com.yunqiic.cocojob.server.core.instance.InstanceLogService;
import com.yunqiic.cocojob.server.core.instance.InstanceManager;
import com.yunqiic.cocojob.server.core.workflow.WorkflowInstanceManager;
import com.yunqiic.cocojob.server.remote.transport.starter.AkkaStarter;
import com.yunqiic.cocojob.server.remote.transport.starter.VertXStarter;
import com.yunqiic.cocojob.server.remote.worker.WorkerClusterQueryService;
import com.yunqiic.cocojob.server.common.module.WorkerInfo;
import com.yunqiic.cocojob.common.response.AskResponse;
import com.yunqiic.cocojob.common.serialize.JsonUtils;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.server.common.constants.SwitchableStatus;
import com.yunqiic.cocojob.server.common.utils.SpringUtils;
import com.yunqiic.cocojob.server.persistence.remote.model.ContainerInfoDO;
import com.yunqiic.cocojob.server.persistence.remote.model.JobInfoDO;
import com.yunqiic.cocojob.server.persistence.remote.repository.ContainerInfoRepository;
import com.yunqiic.cocojob.server.persistence.remote.repository.JobInfoRepository;
import com.yunqiic.cocojob.server.remote.worker.WorkerClusterManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * receive and process worker's request
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@Component
public class WorkerRequestHandler {

    @Resource
    private Environment environment;
    @Resource
    private InstanceManager instanceManager;
    @Resource
    private WorkflowInstanceManager workflowInstanceManager;
    @Resource
    private InstanceLogService instanceLogService;
    @Resource
    private ContainerInfoRepository containerInfoRepository;

    @Resource
    private WorkerClusterQueryService workerClusterQueryService;

    private static WorkerRequestHandler workerRequestHandler;

    @PostConstruct
    public void initHandler() {
        // init akka
        AkkaStarter.actorSystem.actorOf(Props.create(WorkerRequestAkkaHandler.class)
                .withDispatcher("akka.server-actor-dispatcher")
                .withRouter(new RoundRobinPool(Runtime.getRuntime().availableProcessors() * 4)), RemoteConstant.SERVER_ACTOR_NAME);
        // init vert.x
        VertXStarter.vertx.deployVerticle(new WorkerRequestHttpHandler());
    }

    /**
     * ?????? Worker ???????????????
     * @param heartbeat ?????????
     */
    public void onReceiveWorkerHeartbeat(WorkerHeartbeat heartbeat) {
        WorkerClusterManagerService.updateStatus(heartbeat);
    }

    /**
     * ?????? instance ??????
     * @param req ?????????????????????????????????
     */
    public Optional<AskResponse> onReceiveTaskTrackerReportInstanceStatusReq(TaskTrackerReportInstanceStatusReq req) throws ExecutionException {
        // 2021/02/05 ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        if (req.getWfInstanceId() != null && !CollectionUtils.isEmpty(req.getAppendedWfContext())) {
            // ??????????????????????????????
            workflowInstanceManager.updateWorkflowContext(req.getWfInstanceId(),req.getAppendedWfContext());
        }

        instanceManager.updateStatus(req);

        // ?????????????????????/???????????????????????????
        if (InstanceStatus.FINISHED_STATUS.contains(req.getInstanceStatus())) {
            return Optional.of(AskResponse.succeed(null));
        }
        return Optional.empty();
    }

    /**
     * ??????OMS??????????????????
     * @param req ????????????
     */
    public void onReceiveWorkerLogReportReq(WorkerLogReportReq req) {
        // ?????????????????????????????????...????????????????????? + Map#get ???...
        instanceLogService.submitLogs(req.getWorkerAddress(), req.getInstanceLogContents());
    }

    /**
     * ?????? Worker??????????????????
     * @param req ??????????????????
     */
    public AskResponse onReceiveWorkerNeedDeployContainerRequest(WorkerNeedDeployContainerRequest req) {

        String port = environment.getProperty("local.server.port");

        Optional<ContainerInfoDO> containerInfoOpt = containerInfoRepository.findById(req.getContainerId());
        AskResponse askResponse = new AskResponse();
        if (!containerInfoOpt.isPresent() || containerInfoOpt.get().getStatus() != SwitchableStatus.ENABLE.getV()) {
            askResponse.setSuccess(false);
            askResponse.setMessage("can't find container by id: " + req.getContainerId());
        }else {
            ContainerInfoDO containerInfo = containerInfoOpt.get();
            askResponse.setSuccess(true);

            ServerDeployContainerRequest dpReq = new ServerDeployContainerRequest();
            BeanUtils.copyProperties(containerInfo, dpReq);
            dpReq.setContainerId(containerInfo.getId());
            String downloadURL = String.format("http://%s:%s/container/downloadJar?version=%s", NetUtils.getLocalHost(), port, containerInfo.getVersion());
            dpReq.setDownloadURL(downloadURL);

            askResponse.setData(JsonUtils.toBytes(dpReq));
        }
        return askResponse;
    }

    /**
     * ?????? worker ??????????????????????????????????????????????????????
     * @param req jobId + appId
     */
    public AskResponse onReceiveWorkerQueryExecutorClusterReq(WorkerQueryExecutorClusterReq req) {

        AskResponse askResponse;

        Long jobId = req.getJobId();
        Long appId = req.getAppId();

        JobInfoRepository jobInfoRepository = SpringUtils.getBean(JobInfoRepository.class);
        Optional<JobInfoDO> jobInfoOpt = jobInfoRepository.findById(jobId);
        if (jobInfoOpt.isPresent()) {
            JobInfoDO jobInfo = jobInfoOpt.get();
            if (!jobInfo.getAppId().equals(appId)) {
                askResponse = AskResponse.failed("Permission Denied!");
            }else {
                List<String> sortedAvailableWorker = workerClusterQueryService.getSuitableWorkers(jobInfo)
                        .stream().map(WorkerInfo::getAddress).collect(Collectors.toList());
                askResponse = AskResponse.succeed(sortedAvailableWorker);
            }
        }else {
            askResponse = AskResponse.failed("can't find jobInfo by jobId: " + jobId);
        }
        return askResponse;
    }

    public static WorkerRequestHandler getWorkerRequestHandler() {
        if (workerRequestHandler == null) {
            workerRequestHandler = SpringUtils.getBean(WorkerRequestHandler.class);
        }
        return workerRequestHandler;
    }
}
