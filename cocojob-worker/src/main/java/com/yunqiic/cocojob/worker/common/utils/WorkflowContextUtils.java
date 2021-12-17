package com.yunqiic.cocojob.worker.common.utils;

import com.yunqiic.cocojob.common.serialize.JsonUtils;

import java.util.Map;

/**
 * 工作流上下文工具类
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class WorkflowContextUtils {

    private WorkflowContextUtils() {

    }


    public static boolean isExceededLengthLimit(Map<String, String> appendedWfContext, int maxLength) {

        String jsonString = JsonUtils.toJSONString(appendedWfContext);
        if (jsonString == null) {
            // impossible
            return true;
        }

        return maxLength < jsonString.length();

    }

}
