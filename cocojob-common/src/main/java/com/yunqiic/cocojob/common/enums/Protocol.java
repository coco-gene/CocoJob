package com.yunqiic.cocojob.common.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * transport protocol between server and worker
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Getter
public enum Protocol {

    AKKA,
    HTTP;

    public static Protocol of(String protocol) {
        if (StringUtils.isEmpty(protocol)) {
            return AKKA;
        }
        try {
            return Protocol.valueOf(protocol.toUpperCase());
        } catch (Exception ignore) {
        }
        // For compatibility with older versions, the AKKA protocol is used by default
        return AKKA;
    }
}
