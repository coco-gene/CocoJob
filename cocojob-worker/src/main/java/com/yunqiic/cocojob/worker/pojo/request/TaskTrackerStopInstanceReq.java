package com.yunqiic.cocojob.worker.pojo.request;

import com.yunqiic.cocojob.common.PowerSerializable;
import lombok.Data;


/**
 * TaskTracker 停止 ProcessorTracker，释放相关资源
 * 任务执行完毕后停止 OR 手动强制停止
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class TaskTrackerStopInstanceReq implements PowerSerializable {

    private Long instanceId;
    // 保留字段，暂时没用
    private String type;
}
