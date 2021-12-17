package com.yunqiic.cocojob.server.web.request;

import lombok.Data;

/**
 * 查询工作流
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class QueryWorkflowInfoRequest {

    // 任务所属应用ID
    private Long appId;
    // 当前页码
    private Integer index;
    // 页大小
    private Integer pageSize;

    // 查询条件
    private Long workflowId;
    private String keyword;

}
