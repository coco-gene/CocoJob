package com.yunqiic.cocojob.common;

import java.io.Serializable;

/**
 * CocoJob serializable interface.
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public interface PowerSerializable extends Serializable {

    /**
     * request path for http or other protocol, like '/worker/stopInstance'
     * @return null for non-http request object or no-null path for http request needed object
     */
    default String path() {
        return null;
    }
}
