package com.yunqiic.cocojob.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 节点类型
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Getter
@AllArgsConstructor
public enum WorkflowNodeType {
    /**
     * 任务节点
     */
    JOB(1);


    private final int code;

}
