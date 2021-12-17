package com.yunqiic.cocojob.server.remote.server.election;

import com.yunqiic.cocojob.common.PowerSerializable;
import lombok.Data;


/**
 * 检测目标机器是否存活
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class Ping implements PowerSerializable {
    private long currentTime;
}
