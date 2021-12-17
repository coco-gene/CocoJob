package com.yunqiic.cocojob.worker.test;

import com.yunqiic.cocojob.worker.pojo.request.TaskTrackerStartTaskReq;
import org.junit.jupiter.api.Test;


/**
 * 测试任务的启动
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class ProcessorTrackerTest extends CommonTest {

    @Test
    public void testBasicProcessor() throws Exception {

        TaskTrackerStartTaskReq req = genTaskTrackerStartTaskReq("com.yunqiic.cocojob.worker.test.processors.TestBasicProcessor");
        remoteProcessorTracker.tell(req, null);
        Thread.sleep(30000);
    }

    @Test
    public void testMapReduceProcessor() throws Exception {
        TaskTrackerStartTaskReq req = genTaskTrackerStartTaskReq("com.yunqiic.cocojob.worker.test.processors.TestMapReduceProcessor");
        remoteProcessorTracker.tell(req, null);
        Thread.sleep(30000);
    }
}
