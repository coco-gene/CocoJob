package com.yunqiic.cocojob.server.extension.defaultimpl.workerfilter;

import com.yunqiic.cocojob.server.extension.WorkerFilter;
import com.yunqiic.cocojob.server.persistence.remote.model.JobInfoDO;
import com.yunqiic.cocojob.server.common.module.WorkerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * filter disconnected worker
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
@Component
public class DisconnectedWorkerFilter implements WorkerFilter {

    @Override
    public boolean filter(WorkerInfo workerInfo, JobInfoDO jobInfo) {
        boolean timeout = workerInfo.timeout();
        if (timeout) {
            log.info("[Job-{}] filter worker[{}] due to timeout(lastActiveTime={})", jobInfo.getId(), workerInfo.getAddress(), workerInfo.getLastActiveTime());
        }
        return timeout;
    }
}
