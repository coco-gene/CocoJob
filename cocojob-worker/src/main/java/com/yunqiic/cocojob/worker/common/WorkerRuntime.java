package com.yunqiic.cocojob.worker.common;

import akka.actor.ActorSystem;
import com.yunqiic.cocojob.worker.background.OmsLogHandler;
import com.yunqiic.cocojob.worker.background.ServerDiscoveryService;
import com.yunqiic.cocojob.worker.persistence.TaskPersistenceService;
import lombok.Data;

/**
 * store worker's runtime
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class WorkerRuntime {

    private Long appId;

    private CocoJobWorkerConfig workerConfig;

    private String workerAddress;

    private ActorSystem actorSystem;
    private OmsLogHandler omsLogHandler;
    private ServerDiscoveryService serverDiscoveryService;
    private TaskPersistenceService taskPersistenceService;
}
