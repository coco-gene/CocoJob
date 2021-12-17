package com.yunqiic.cocojob.worker.test;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.yunqiic.cocojob.common.enums.ExecuteType;
import com.yunqiic.cocojob.common.RemoteConstant;
import com.yunqiic.cocojob.common.enums.TimeExpressionType;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.worker.CocoJobWorker;
import com.yunqiic.cocojob.worker.common.CocoJobWorkerConfig;
import com.yunqiic.cocojob.worker.common.utils.AkkaUtils;
import com.google.common.collect.Lists;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * description
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class FrequentTaskTrackerTest {

    private static ActorSelection remoteTaskTracker;

    @BeforeAll
    public static void init() throws Exception {

        CocoJobWorkerConfig workerConfig = new CocoJobWorkerConfig();
        workerConfig.setAppName("oms-test");
        workerConfig.setServerAddress(Lists.newArrayList("127.0.0.1:7700"));
        CocoJobWorker worker = new CocoJobWorker();
        worker.setConfig(workerConfig);
        worker.init();

        ActorSystem testAS = ActorSystem.create("oms-test", ConfigFactory.load("oms-akka-test.conf"));
        String akkaRemotePath = AkkaUtils.getAkkaWorkerPath(NetUtils.getLocalHost() + ":" + RemoteConstant.DEFAULT_WORKER_PORT, RemoteConstant.TASK_TRACKER_ACTOR_NAME);
        remoteTaskTracker = testAS.actorSelection(akkaRemotePath);
    }

    @Test
    public void testFixRateJob() throws Exception {
        remoteTaskTracker.tell(TestUtils.genServerScheduleJobReq(ExecuteType.STANDALONE, TimeExpressionType.FIXED_RATE), null);
        Thread.sleep(5000000);
    }

    @Test
    public void testFixDelayJob() throws Exception {
        remoteTaskTracker.tell(TestUtils.genServerScheduleJobReq(ExecuteType.MAP_REDUCE, TimeExpressionType.FIXED_DELAY), null);
        Thread.sleep(5000000);
    }
}
