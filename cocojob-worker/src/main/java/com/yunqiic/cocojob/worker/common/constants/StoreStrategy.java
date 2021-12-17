package com.yunqiic.cocojob.worker.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 持久化策略
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Getter
@AllArgsConstructor
public enum  StoreStrategy {

    DISK("磁盘"),
    MEMORY("内存");

    private final String des;
}
