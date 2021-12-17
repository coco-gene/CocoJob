package com.yunqiic.cocojob.worker.test.function;

import com.yunqiic.cocojob.worker.test.CommonTest;
import com.yunqiic.cocojob.worker.test.TestUtils;
import com.yunqiic.cocojob.common.enums.ExecuteType;
import com.yunqiic.cocojob.common.enums.TimeExpressionType;
import com.yunqiic.cocojob.common.request.ServerScheduleJobReq;
import com.yunqiic.cocojob.worker.common.WorkerRuntime;
import com.yunqiic.cocojob.worker.core.tracker.processor.ProcessorTracker;
import com.yunqiic.cocojob.worker.core.tracker.task.TaskTracker;
import com.yunqiic.cocojob.worker.pojo.request.ProcessorTrackerStatusReportReq;
import com.yunqiic.cocojob.worker.pojo.request.TaskTrackerStartTaskReq;
import org.junit.jupiter.api.Test;

/**
 * 空闲测试
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class IdleTest extends CommonTest {

    @Test
    public void testProcessorTrackerSendIdleReport() throws Exception {
        TaskTrackerStartTaskReq req = genTaskTrackerStartTaskReq("com.yunqiic.cocojob.worker.test.processors.TestBasicProcessor");
        ProcessorTracker pt = new ProcessorTracker(req, new WorkerRuntime());
        Thread.sleep(300000);
    }

    @Test
    public void testTaskTrackerProcessorIdle() throws Exception {

        ProcessorTrackerStatusReportReq req = ProcessorTrackerStatusReportReq.buildIdleReport(10086L);
        ServerScheduleJobReq serverScheduleJobReq = TestUtils.genServerScheduleJobReq(ExecuteType.STANDALONE, TimeExpressionType.API);

        TaskTracker taskTracker = TaskTracker.create(serverScheduleJobReq, new WorkerRuntime());
        if (taskTracker != null) {
            taskTracker.receiveProcessorTrackerHeartbeat(req);
        }
    }
}
