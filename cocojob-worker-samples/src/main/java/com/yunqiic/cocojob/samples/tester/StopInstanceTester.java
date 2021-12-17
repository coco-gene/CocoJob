package com.yunqiic.cocojob.samples.tester;

import com.yunqiic.cocojob.worker.core.processor.ProcessResult;
import com.yunqiic.cocojob.worker.core.processor.TaskContext;
import com.yunqiic.cocojob.worker.core.processor.sdk.BasicProcessor;
import org.springframework.stereotype.Component;

/**
 * 测试用户反馈的无法停止实例的问题
 * https://github.com/coco-gene/CocoJob/issues/37
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Component
public class StopInstanceTester implements BasicProcessor {
    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        int i = 0;
        while (true) {
            System.out.println(i++);
            Thread.sleep(1000*10);
        }
    }
}
