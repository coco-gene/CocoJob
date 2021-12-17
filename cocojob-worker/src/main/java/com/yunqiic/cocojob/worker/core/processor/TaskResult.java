package com.yunqiic.cocojob.worker.core.processor;

import lombok.Data;

/**
 * Task执行结果
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class TaskResult {

    private String taskId;
    private boolean success;
    private String result;

}
