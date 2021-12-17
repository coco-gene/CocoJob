package com.yunqiic.cocojob.worker.log.impl;

import com.yunqiic.cocojob.worker.log.OmsLogger;
import lombok.extern.slf4j.Slf4j;

/**
 * for local test
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
public class OmsLocalLogger implements OmsLogger {
    @Override
    public void debug(String messagePattern, Object... args) {
        log.debug(messagePattern, args);
    }

    @Override
    public void info(String messagePattern, Object... args) {
        log.info(messagePattern, args);
    }

    @Override
    public void warn(String messagePattern, Object... args) {
        log.warn(messagePattern, args);
    }

    @Override
    public void error(String messagePattern, Object... args) {
        log.error(messagePattern, args);
    }
}
