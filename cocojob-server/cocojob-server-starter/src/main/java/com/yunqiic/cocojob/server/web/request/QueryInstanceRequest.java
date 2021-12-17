package com.yunqiic.cocojob.server.web.request;

import com.yunqiic.cocojob.server.common.constants.InstanceType;
import lombok.Data;

/**
 * 任务实例查询对象
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class QueryInstanceRequest {

    // 任务所属应用ID
    private Long appId;
    // 当前页码
    private Integer index;
    // 页大小
    private Integer pageSize;

    // 查询条件（NORMAL/WORKFLOW）
    private InstanceType type;
    private Long instanceId;
    private Long jobId;
    private Long wfInstanceId;

    private String status;
}
