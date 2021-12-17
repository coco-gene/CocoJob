package com.yunqiic.cocojob.worker.test.processors;

import com.yunqiic.cocojob.common.serialize.JsonUtils;
import com.yunqiic.cocojob.worker.core.processor.ProcessResult;
import com.yunqiic.cocojob.worker.core.processor.TaskContext;
import com.yunqiic.cocojob.worker.core.processor.sdk.BasicProcessor;

/**
 * 测试用的基础处理器
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class TestBasicProcessor implements BasicProcessor {

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        System.out.println("======== BasicProcessor#process ========");
        System.out.println("TaskContext: " + JsonUtils.toJSONString(context) + ";time = " + System.currentTimeMillis());
        return new ProcessResult(true, System.currentTimeMillis() + "success");
    }


}
