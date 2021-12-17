package com.yunqiic.cocojob.server.common.timewheel.holder;

import com.yunqiic.cocojob.server.common.timewheel.HashedWheelTimer;

/**
 * 时间轮单例
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class HashedWheelTimerHolder {

    // 非精确时间轮，每 5S 走一格
    public static final HashedWheelTimer INACCURATE_TIMER = new HashedWheelTimer(5, 16, 0);

    private HashedWheelTimerHolder() {
    }
}
