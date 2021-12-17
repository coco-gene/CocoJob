package com.yunqiic.cocojob.server.remote.transport;

import com.yunqiic.cocojob.common.PowerSerializable;
import com.yunqiic.cocojob.common.enums.Protocol;
import com.yunqiic.cocojob.common.response.AskResponse;

/**
 * Transporter
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public interface Transporter {

    Protocol getProtocol();

    String getAddress();

    void tell(String address, PowerSerializable object);

    AskResponse ask(String address, PowerSerializable object) throws Exception;
}
