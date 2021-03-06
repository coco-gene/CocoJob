package com.yunqiic.cocojob.server.core.lock;

import com.yunqiic.cocojob.common.utils.SegmentLock;
import org.springframework.core.annotation.Order;
import com.yunqiic.cocojob.server.common.utils.AOPUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * aspect for @UseSegmentLock
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class UseSegmentLockAspect {

    private final Map<String, SegmentLock> lockStore = Maps.newConcurrentMap();

    @Around(value = "@annotation(useSegmentLock))")
    public Object execute(ProceedingJoinPoint point, UseSegmentLock useSegmentLock) throws Throwable {
        SegmentLock segmentLock = lockStore.computeIfAbsent(useSegmentLock.type(), ignore -> {
            int concurrencyLevel = useSegmentLock.concurrencyLevel();
            log.info("[UseSegmentLockAspect] create SegmentLock for [{}] with concurrencyLevel: {}", useSegmentLock.type(), concurrencyLevel);
            return new SegmentLock(concurrencyLevel);
        });

        int index = AOPUtils.parseSpEl(AOPUtils.parseMethod(point), point.getArgs(), useSegmentLock.key(), Integer.class, 1);
        try {
            segmentLock.lockInterruptibleSafe(index);
            return point.proceed();
        } finally {
            segmentLock.unlock(index);
        }
    }
}
