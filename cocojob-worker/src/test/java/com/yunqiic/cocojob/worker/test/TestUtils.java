package com.yunqiic.cocojob.worker.test;

import com.yunqiic.cocojob.common.enums.ExecuteType;
import com.yunqiic.cocojob.common.enums.ProcessorType;
import com.yunqiic.cocojob.common.RemoteConstant;
import com.yunqiic.cocojob.common.enums.TimeExpressionType;
import com.yunqiic.cocojob.common.request.ServerScheduleJobReq;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.google.common.collect.Lists;

/**
 * 测试需要用到的工具类
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class TestUtils {

    public static ServerScheduleJobReq genServerScheduleJobReq(ExecuteType executeType, TimeExpressionType timeExpressionType) {
        ServerScheduleJobReq req = new ServerScheduleJobReq();

        req.setJobId(1L);
        req.setInstanceId(10086L);
        req.setAllWorkerAddress(Lists.newArrayList(NetUtils.getLocalHost() + ":" + RemoteConstant.DEFAULT_WORKER_PORT));

        req.setJobParams("JobParams");
        req.setInstanceParams("InstanceParams");
        req.setProcessorType(ProcessorType.BUILT_IN.name());
        req.setTaskRetryNum(3);
        req.setThreadConcurrency(10);
        req.setInstanceTimeoutMS(500000);
        req.setTimeExpressionType(timeExpressionType.name());
        switch (timeExpressionType) {
            case CRON:req.setTimeExpression("0 * * * * ? ");
            case FIXED_RATE:
            case FIXED_DELAY:req.setTimeExpression("5000");
        }

        switch (executeType) {
            case STANDALONE:
                req.setExecuteType(ExecuteType.STANDALONE.name());
                req.setProcessorInfo("com.yunqiic.cocojob.worker.test.processors.TestBasicProcessor");
                break;
            case MAP_REDUCE:
                req.setExecuteType(ExecuteType.MAP_REDUCE.name());
                req.setProcessorInfo("com.yunqiic.cocojob.worker.test.processors.TestMapReduceProcessor");
                break;
            case BROADCAST:
                req.setExecuteType(ExecuteType.BROADCAST.name());
                req.setProcessorInfo("com.yunqiic.cocojob.worker.test.processors.TestBroadcastProcessor");
                break;
        }

        return req;
    }

}
