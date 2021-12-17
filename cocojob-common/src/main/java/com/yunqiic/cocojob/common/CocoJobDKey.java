package com.yunqiic.cocojob.common;

import java.net.NetworkInterface;

/**
 * 通过 JVM 启动参数传入的配置信息
 *
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class CocoJobDKey {

    /**
     * The property name for {@link NetworkInterface#getDisplayName() the name of network interface} that the CocoJob application prefers
     */
    public static final String PREFERRED_NETWORK_INTERFACE = "cocojob.network.interface.preferred";

    public static final String BIND_LOCAL_ADDRESS = "cocojob.network.local.address";

    /**
     * Java regular expressions for network interfaces that will be ignored.
     */
    public static final String IGNORED_NETWORK_INTERFACE_REGEX = "cocojob.network.interface.ignored";

    public static final String WORKER_STATUS_CHECK_PERIOD = "cocojob.worker.status-check.normal.period";

}
