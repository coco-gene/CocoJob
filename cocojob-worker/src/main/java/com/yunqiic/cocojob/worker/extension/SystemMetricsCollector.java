package com.yunqiic.cocojob.worker.extension;

import com.yunqiic.cocojob.common.model.SystemMetrics;

/**
 * user-customized system metrics collector
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public interface SystemMetricsCollector {

    /**
     * SystemMetrics, you can put your custom metrics info in the 'extra' param
     * @return SystemMetrics
     */
    SystemMetrics collect();
}
