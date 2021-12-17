package com.yunqiic.cocojob.common.request;

import com.yunqiic.cocojob.common.PowerSerializable;
import com.yunqiic.cocojob.common.ProtocolConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 服务器要求任务实例停止执行请求
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerStopInstanceReq implements PowerSerializable {
    private Long instanceId;

    @Override
    public String path() {
        return ProtocolConstant.WORKER_PATH_STOP_INSTANCE;
    }
}
