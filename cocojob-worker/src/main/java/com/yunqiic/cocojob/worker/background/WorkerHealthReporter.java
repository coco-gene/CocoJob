package com.yunqiic.cocojob.worker.background;

import akka.actor.ActorSelection;
import com.yunqiic.cocojob.common.enums.Protocol;
import com.yunqiic.cocojob.common.model.SystemMetrics;
import com.yunqiic.cocojob.common.request.WorkerHeartbeat;
import com.yunqiic.cocojob.worker.common.CocoJobWorkerVersion;
import com.yunqiic.cocojob.worker.common.WorkerRuntime;
import com.yunqiic.cocojob.worker.common.utils.AkkaUtils;
import com.yunqiic.cocojob.worker.common.utils.SystemInfoUtils;
import com.yunqiic.cocojob.worker.container.OmsContainerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Worker健康度定时上报
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@AllArgsConstructor
public class WorkerHealthReporter implements Runnable {

    private final WorkerRuntime workerRuntime;

    @Override
    public void run() {

        // 没有可用Server，无法上报
        String currentServer = workerRuntime.getServerDiscoveryService().getCurrentServerAddress();
        if (StringUtils.isEmpty(currentServer)) {
            return;
        }

        SystemMetrics systemMetrics;

        if (workerRuntime.getWorkerConfig().getSystemMetricsCollector() == null) {
            systemMetrics = SystemInfoUtils.getSystemMetrics();
        } else {
            systemMetrics = workerRuntime.getWorkerConfig().getSystemMetricsCollector().collect();
        }

        WorkerHeartbeat heartbeat = new WorkerHeartbeat();

        heartbeat.setSystemMetrics(systemMetrics);
        heartbeat.setWorkerAddress(workerRuntime.getWorkerAddress());
        heartbeat.setAppName(workerRuntime.getWorkerConfig().getAppName());
        heartbeat.setAppId(workerRuntime.getAppId());
        heartbeat.setHeartbeatTime(System.currentTimeMillis());
        heartbeat.setVersion(CocoJobWorkerVersion.getVersion());
        heartbeat.setProtocol(Protocol.AKKA.name());
        heartbeat.setClient("Atlantis");
        heartbeat.setTag(workerRuntime.getWorkerConfig().getTag());

        // 获取当前加载的容器列表
        heartbeat.setContainerInfos(OmsContainerFactory.getDeployedContainerInfos());

        // 发送请求
        String serverPath = AkkaUtils.getServerActorPath(currentServer);
        if (StringUtils.isEmpty(serverPath)) {
            return;
        }
        ActorSelection actorSelection = workerRuntime.getActorSystem().actorSelection(serverPath);
        actorSelection.tell(heartbeat, null);
    }
}
