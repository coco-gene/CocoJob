package com.yunqiic.cocojob.official.processors;

import com.yunqiic.cocojob.worker.core.processor.TaskContext;
import com.yunqiic.cocojob.worker.core.processor.WorkflowContext;
import com.yunqiic.cocojob.worker.log.impl.OmsLocalLogger;

import java.util.concurrent.ThreadLocalRandom;

/**
 * TestUtils
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class TestUtils {

    public static TaskContext genTaskContext(String jobParams) {

        long jobId = ThreadLocalRandom.current().nextLong();

        TaskContext taskContext = new TaskContext();
        taskContext.setJobId(jobId);
        taskContext.setInstanceId(jobId);
        taskContext.setJobParams(jobParams);
        taskContext.setTaskId("0.0");
        taskContext.setTaskName("TEST_TASK");
        taskContext.setOmsLogger(new OmsLocalLogger());
        taskContext.setWorkflowContext(new WorkflowContext(null, null));
        return taskContext;
    }
}
