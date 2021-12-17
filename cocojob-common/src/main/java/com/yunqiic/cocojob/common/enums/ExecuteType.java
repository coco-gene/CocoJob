package com.yunqiic.cocojob.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Execution type.
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Getter
@AllArgsConstructor
public enum ExecuteType {
    /**
     * Standalone type of task.
     */
    STANDALONE(1, "单机执行"),
    /**
     * Broadcast type of task.
     */
    BROADCAST(2, "广播执行"),
    /**
     * MapReduce type of task.
     */
    MAP_REDUCE(3, "MapReduce"),
    MAP(4, "Map");

    int v;
    String des;

    public static ExecuteType of(int v) {
        for (ExecuteType type : values()) {
            if (type.v == v) {
                return type;
            }
        }
        throw new IllegalArgumentException("unknown ExecuteType of " + v);
    }
}
