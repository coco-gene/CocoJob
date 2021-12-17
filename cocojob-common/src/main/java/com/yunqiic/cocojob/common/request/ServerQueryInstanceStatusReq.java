package com.yunqiic.cocojob.common.request;

import com.yunqiic.cocojob.common.PowerSerializable;
import com.yunqiic.cocojob.common.ProtocolConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务器查询实例运行状态，需要返回详细的运行数据
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerQueryInstanceStatusReq implements PowerSerializable {
    private Long instanceId;

    @Override
    public String path() {
        return ProtocolConstant.WORKER_PATH_QUERY_INSTANCE_INFO;
    }
}
