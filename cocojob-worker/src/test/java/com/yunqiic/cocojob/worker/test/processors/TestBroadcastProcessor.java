package com.yunqiic.cocojob.worker.test.processors;

import com.yunqiic.cocojob.common.serialize.JsonUtils;
import com.yunqiic.cocojob.worker.core.processor.ProcessResult;
import com.yunqiic.cocojob.worker.core.processor.TaskContext;
import com.yunqiic.cocojob.worker.core.processor.TaskResult;
import com.yunqiic.cocojob.worker.core.processor.sdk.BroadcastProcessor;

import java.util.List;

/**
 * 测试用的广播执行处理器
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class TestBroadcastProcessor implements BroadcastProcessor {
    @Override
    public ProcessResult preProcess(TaskContext taskContext) throws Exception {
        System.out.println("=============== TestBroadcastProcessor#preProcess ===============");
        System.out.println("taskContext:" + JsonUtils.toJSONString(taskContext));
        return new ProcessResult(true, "preProcess success");
    }

    @Override
    public ProcessResult postProcess(TaskContext taskContext, List<TaskResult> taskResults) throws Exception {
        System.out.println("=============== TestBroadcastProcessor#postProcess ===============");
        System.out.println("taskContext:" + JsonUtils.toJSONString(taskContext));
        System.out.println("taskId2Result:" + taskResults);
        return new ProcessResult(true, "postProcess success");
    }

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        System.out.println("=============== TestBroadcastProcessor#process ===============");
        System.out.println("taskContext:" + JsonUtils.toJSONString(context));
        return new ProcessResult(true, "processSuccess");
    }
}
