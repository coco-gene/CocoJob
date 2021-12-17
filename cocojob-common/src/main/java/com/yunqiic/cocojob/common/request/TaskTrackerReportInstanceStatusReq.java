package com.yunqiic.cocojob.common.request;

import com.yunqiic.cocojob.common.PowerSerializable;
import lombok.Data;

import java.util.Map;


/**
 * TaskTracker 将状态上报给服务器
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class TaskTrackerReportInstanceStatusReq implements PowerSerializable {

    private Long jobId;

    private Long instanceId;

    private Long wfInstanceId;
    /**
     * 追加的工作流上下文数据
     * @since 2021-12-02
     */
    private Map<String,String> appendedWfContext;

    private int instanceStatus;

    private String result;

    /* ********* 统计信息 ********* */

    private long totalTaskNum;

    private long succeedTaskNum;

    private long failedTaskNum;

    private long startTime;

    private long reportTime;

    private String sourceAddress;
}
