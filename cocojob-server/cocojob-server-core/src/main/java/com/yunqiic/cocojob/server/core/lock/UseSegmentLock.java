package com.yunqiic.cocojob.server.core.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * use segment lock to make concurrent safe
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseSegmentLock {

    String type();

    String key();

    int concurrencyLevel();
}
