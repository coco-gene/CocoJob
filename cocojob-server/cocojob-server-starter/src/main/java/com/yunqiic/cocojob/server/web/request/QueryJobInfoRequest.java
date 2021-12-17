package com.yunqiic.cocojob.server.web.request;

import lombok.Data;

/**
 * 查询任务列表
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class QueryJobInfoRequest {

    // 任务所属应用ID
    private Long appId;
    // 当前页码
    private Integer index;
    // 页大小
    private Integer pageSize;

    // 查询条件
    private Long jobId;
    private String keyword;
}
