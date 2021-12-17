package com.yunqiic.cocojob.server.remote.transport.starter;

import com.yunqiic.cocojob.common.OmsConstant;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.server.common.CocoJobServerConfigKey;
import com.yunqiic.cocojob.server.common.utils.PropertyUtils;
import com.google.common.base.Stopwatch;
import io.vertx.core.Vertx;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * vert.x starter
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
public class VertXStarter {

    public static Vertx vertx;
    @Getter
    private static String address;

    public static void init() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("[CocoJob] CocoJob's vert.x system start to bootstrap...");

        Properties properties = PropertyUtils.getProperties();
        int port = Integer.parseInt(properties.getProperty(CocoJobServerConfigKey.HTTP_PORT, String.valueOf(OmsConstant.SERVER_DEFAULT_HTTP_PORT)));
        String portFromJVM = System.getProperty(CocoJobServerConfigKey.HTTP_PORT);
        if (StringUtils.isNotEmpty(portFromJVM)) {
            port = Integer.parseInt(portFromJVM);
        }
        String localIP = NetUtils.getLocalHost();

        address = localIP + ":" + port;
        log.info("[CocoJob] vert.x server address: {}", address);

        vertx = Vertx.vertx();

        log.info("[CocoJob] CocoJob's vert.x system started successfully, using time {}.", stopwatch);
    }
}
