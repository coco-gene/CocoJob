package com.yunqiic.cocojob.worker.test;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.yunqiic.cocojob.common.enums.ExecuteType;
import com.yunqiic.cocojob.common.enums.ProcessorType;
import com.yunqiic.cocojob.common.RemoteConstant;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.worker.CocoJobWorker;
import com.yunqiic.cocojob.worker.common.CocoJobWorkerConfig;
import com.yunqiic.cocojob.worker.common.utils.AkkaUtils;
import com.yunqiic.cocojob.worker.pojo.model.InstanceInfo;
import com.yunqiic.cocojob.worker.pojo.request.TaskTrackerStartTaskReq;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * 启动公共服务
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class CommonTest {

    protected static ActorSelection remoteProcessorTracker;
    protected static ActorSelection remoteTaskTracker;

    @BeforeAll
    public static void startWorker() throws Exception {
        CocoJobWorkerConfig workerConfig = new CocoJobWorkerConfig();
        workerConfig.setAppName("oms-test");
        workerConfig.setEnableTestMode(true);

        CocoJobWorker worker = new CocoJobWorker();
        worker.setConfig(workerConfig);
        worker.init();

        ActorSystem testAS = ActorSystem.create("oms-test", ConfigFactory.load("oms-akka-test.conf"));
        String address = NetUtils.getLocalHost() + ":27777";

        remoteProcessorTracker = testAS.actorSelection(AkkaUtils.getAkkaWorkerPath(address, RemoteConstant.PROCESSOR_TRACKER_ACTOR_NAME));
        remoteTaskTracker = testAS.actorSelection(AkkaUtils.getAkkaWorkerPath(address, RemoteConstant.TASK_TRACKER_ACTOR_NAME));
    }

    @AfterAll
    public static void stop() throws Exception {
        Thread.sleep(120000);
    }

    public static TaskTrackerStartTaskReq genTaskTrackerStartTaskReq(String processor) {

        InstanceInfo instanceInfo = new InstanceInfo();

        instanceInfo.setJobId(1L);
        instanceInfo.setInstanceId(10086L);

        instanceInfo.setExecuteType(ExecuteType.STANDALONE.name());
        instanceInfo.setProcessorType(ProcessorType.BUILT_IN.name());
        instanceInfo.setProcessorInfo(processor);

        instanceInfo.setInstanceTimeoutMS(500000);

        instanceInfo.setThreadConcurrency(5);
        instanceInfo.setTaskRetryNum(3);

        TaskTrackerStartTaskReq req = new TaskTrackerStartTaskReq();

        req.setTaskTrackerAddress(NetUtils.getLocalHost() + ":27777");
        req.setInstanceInfo(instanceInfo);

        req.setTaskId("0");
        req.setTaskName("ROOT_TASK");
        req.setTaskCurrentRetryNums(0);

        return req;
    }
}
