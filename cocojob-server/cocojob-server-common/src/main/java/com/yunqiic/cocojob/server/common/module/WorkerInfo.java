package com.yunqiic.cocojob.server.common.module;

import com.yunqiic.cocojob.common.model.DeployedContainerInfo;
import com.yunqiic.cocojob.common.model.SystemMetrics;
import com.yunqiic.cocojob.common.request.WorkerHeartbeat;
import lombok.Data;

import java.util.List;

/**
 * worker info
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class WorkerInfo {

    private String address;

    private long lastActiveTime;

    private String protocol;

    private String client;

    private String tag;

    private SystemMetrics systemMetrics;

    private List<DeployedContainerInfo> containerInfos;

    private static final long WORKER_TIMEOUT_MS = 60000;

    public void refresh(WorkerHeartbeat workerHeartbeat) {
        address = workerHeartbeat.getWorkerAddress();
        lastActiveTime = workerHeartbeat.getHeartbeatTime();
        protocol = workerHeartbeat.getProtocol();
        client = workerHeartbeat.getClient();
        tag = workerHeartbeat.getTag();
        systemMetrics = workerHeartbeat.getSystemMetrics();
        containerInfos = workerHeartbeat.getContainerInfos();
    }

    public boolean timeout() {
        long timeout = System.currentTimeMillis() - lastActiveTime;
        return timeout > WORKER_TIMEOUT_MS;
    }
}
