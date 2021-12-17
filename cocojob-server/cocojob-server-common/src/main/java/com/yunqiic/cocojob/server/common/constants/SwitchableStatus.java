package com.yunqiic.cocojob.server.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支持开/关的状态，如 任务状态（JobStatus）和工作流状态（WorkflowStatus）
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Getter
@AllArgsConstructor
public enum SwitchableStatus {
    /**
     *
     */
    ENABLE(1),
    DISABLE(2),
    DELETED(99);

    private final int v;

    public static SwitchableStatus of(int v) {
        for (SwitchableStatus type : values()) {
            if (type.v == v) {
                return type;
            }
        }
        throw new IllegalArgumentException("unknown SwitchableStatus of " + v);
    }
}
