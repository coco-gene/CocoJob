package com.yunqiic.cocojob.official.processors.util;

import com.yunqiic.cocojob.worker.core.processor.TaskContext;
import org.apache.commons.lang3.StringUtils;

/**
 * CommonUtils
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class CommonUtils {

    private CommonUtils() {

    }

    public static String parseParams(TaskContext context) {
        // 工作流中的总是优先使用 jobParams
        if (context.getWorkflowContext().getWfInstanceId() != null) {
            return context.getJobParams();
        }
        if (StringUtils.isNotEmpty(context.getInstanceParams())) {
            return context.getInstanceParams();
        }
        return context.getJobParams();
    }
}
