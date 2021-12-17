package com.yunqiic.cocojob.server.remote.server.redirector;

import com.yunqiic.cocojob.common.PowerSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 原创执行命令
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Getter
@Setter
@Accessors(chain = true)
public class RemoteProcessReq implements PowerSerializable {

    private String className;
    private String methodName;
    private String[] parameterTypes;

    private Object[] args;

}
