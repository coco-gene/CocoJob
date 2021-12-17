package com.yunqiic.cocojob.samples.processors;

import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.worker.core.processor.ProcessResult;
import com.yunqiic.cocojob.worker.core.processor.TaskContext;
import com.yunqiic.cocojob.worker.core.processor.TaskResult;
import com.yunqiic.cocojob.worker.core.processor.sdk.BroadcastProcessor;
import com.yunqiic.cocojob.worker.log.OmsLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 广播处理器 示例
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@Component
public class BroadcastProcessorDemo implements BroadcastProcessor {

    @Override
    public ProcessResult preProcess(TaskContext context) throws Exception {
        System.out.println("===== BroadcastProcessorDemo#preProcess ======");
        context.getOmsLogger().info("BroadcastProcessorDemo#preProcess, current host: {}", NetUtils.getLocalHost());
        if ("rootFailed".equals(context.getJobParams())) {
            return new ProcessResult(false, "console need failed");
        }else {
            return new ProcessResult(true);
        }
    }

    @Override
    public ProcessResult process(TaskContext taskContext) throws Exception {
        OmsLogger logger = taskContext.getOmsLogger();
        System.out.println("===== BroadcastProcessorDemo#process ======");
        logger.info("BroadcastProcessorDemo#process, current host: {}", NetUtils.getLocalHost());
        long sleepTime = 1000;
        try {
            sleepTime = Long.parseLong(taskContext.getJobParams());
        }catch (Exception e) {
            logger.warn("[BroadcastProcessor] parse sleep time failed!", e);
        }
        Thread.sleep(Math.max(sleepTime, 1000));
        return new ProcessResult(true);
    }

    @Override
    public ProcessResult postProcess(TaskContext context, List<TaskResult> taskResults) throws Exception {
        System.out.println("===== BroadcastProcessorDemo#postProcess ======");
        context.getOmsLogger().info("BroadcastProcessorDemo#postProcess, current host: {}, taskResult: {}", NetUtils.getLocalHost(), taskResults);
        return new ProcessResult(true, "success");
    }
}
