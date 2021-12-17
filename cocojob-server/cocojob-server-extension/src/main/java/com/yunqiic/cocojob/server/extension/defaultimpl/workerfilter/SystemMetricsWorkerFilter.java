package com.yunqiic.cocojob.server.extension.defaultimpl.workerfilter;

import com.yunqiic.cocojob.common.model.SystemMetrics;
import com.yunqiic.cocojob.server.extension.WorkerFilter;
import com.yunqiic.cocojob.server.persistence.remote.model.JobInfoDO;
import com.yunqiic.cocojob.server.common.module.WorkerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * filter worker by system metric
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@Component
public class SystemMetricsWorkerFilter implements WorkerFilter {

    @Override
    public boolean filter(WorkerInfo workerInfo, JobInfoDO jobInfo) {
        SystemMetrics metrics = workerInfo.getSystemMetrics();
        boolean filter = !metrics.available(jobInfo.getMinCpuCores(), jobInfo.getMinMemorySpace(), jobInfo.getMinDiskSpace());
        if (filter) {
            log.info("[Job-{}] filter worker[{}] because the {} do not meet the requirements", jobInfo.getId(), workerInfo.getAddress(), workerInfo.getSystemMetrics());
        }
        return filter;
    }
}
