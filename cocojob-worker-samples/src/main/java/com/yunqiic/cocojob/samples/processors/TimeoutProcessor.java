package com.yunqiic.cocojob.samples.processors;

import com.yunqiic.cocojob.worker.core.processor.ProcessResult;
import com.yunqiic.cocojob.worker.core.processor.TaskContext;
import com.yunqiic.cocojob.worker.core.processor.sdk.BasicProcessor;
import org.springframework.stereotype.Component;

/**
 * 测试超时任务
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Component
public class TimeoutProcessor implements BasicProcessor {
    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        Thread.sleep(Long.parseLong(context.getJobParams()));
        return new ProcessResult(true, "impossible~~~~QAQ~");
    }
}
