package com.yunqiic.cocojob.server.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务实例类型
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Getter
@AllArgsConstructor
public enum  InstanceType {

    NORMAL(1),
    WORKFLOW(2);

    private final int v;

}
