package com.yunqiic.cocojob.common.request;

import com.yunqiic.cocojob.common.PowerSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * worker 查询 执行器集群（动态上线需要）
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerQueryExecutorClusterReq implements PowerSerializable {
    private Long appId;
    private Long jobId;
}
