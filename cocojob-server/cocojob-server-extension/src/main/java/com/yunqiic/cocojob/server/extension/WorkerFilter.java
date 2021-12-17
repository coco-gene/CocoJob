package com.yunqiic.cocojob.server.extension;

import com.yunqiic.cocojob.server.persistence.remote.model.JobInfoDO;
import com.yunqiic.cocojob.server.common.module.WorkerInfo;

/**
 * filter worker by system metrics or other info
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public interface WorkerFilter {

    /**
     *
     * @param workerInfo worker info, maybe you need to use your customized info in SystemMetrics#extra
     * @param jobInfoDO job info
     * @return true will remove the worker in process list
     */
    boolean filter(WorkerInfo workerInfo, JobInfoDO jobInfoDO);
}
