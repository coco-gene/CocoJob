package com.yunqiic.cocojob.server.remote.transport.starter;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.yunqiic.cocojob.common.OmsConstant;
import com.yunqiic.cocojob.common.RemoteConstant;
import com.yunqiic.cocojob.common.utils.NetUtils;
import com.yunqiic.cocojob.server.common.CocoJobServerConfigKey;
import com.yunqiic.cocojob.server.common.utils.PropertyUtils;
import com.yunqiic.cocojob.server.remote.server.FriendRequestHandler;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Properties;

/**
 * 服务端 ActorSystem 启动器
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
public class AkkaStarter {

    public static ActorSystem actorSystem;
    @Getter
    private static String actorSystemAddress;

    private static final String AKKA_PATH = "akka://%s@%s/user/%s";

    public static void init() {

        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("[CocoJob] CocoJob's akka system start to bootstrap...");

        // 忽略了一个问题，机器是没办法访问外网的，除非架设自己的NTP服务器
        // TimeUtils.check();

        // 解析配置文件
        Properties properties = PropertyUtils.getProperties();
        int port = Integer.parseInt(properties.getProperty(CocoJobServerConfigKey.AKKA_PORT, String.valueOf(OmsConstant.SERVER_DEFAULT_AKKA_PORT)));
        String portFromJVM = System.getProperty(CocoJobServerConfigKey.AKKA_PORT);
        if (StringUtils.isNotEmpty(portFromJVM)) {
            log.info("[CocoJob] use port from jvm params: {}", portFromJVM);
            port = Integer.parseInt(portFromJVM);
        }

        // 启动 ActorSystem
        Map<String, Object> overrideConfig = Maps.newHashMap();
        String localIP = NetUtils.getLocalHost();
        overrideConfig.put("akka.remote.artery.canonical.hostname", localIP);
        overrideConfig.put("akka.remote.artery.canonical.port", port);
        actorSystemAddress = localIP + ":" + port;
        log.info("[CocoJob] akka-remote server address: {}", actorSystemAddress);

        Config akkaBasicConfig = ConfigFactory.load(RemoteConstant.SERVER_AKKA_CONFIG_NAME);
        Config akkaFinalConfig = ConfigFactory.parseMap(overrideConfig).withFallback(akkaBasicConfig);
        actorSystem = ActorSystem.create(RemoteConstant.SERVER_ACTOR_SYSTEM_NAME, akkaFinalConfig);

        actorSystem.actorOf(Props.create(FriendRequestHandler.class), RemoteConstant.SERVER_FRIEND_ACTOR_NAME);

        log.info("[CocoJob] CocoJob's akka system started successfully, using time {}.", stopwatch);
    }

    /**
     * 获取 ServerActor 的 ActorSelection
     * @param address IP:port
     * @return ActorSelection
     */
    public static ActorSelection getFriendActor(String address) {
        String path = String.format(AKKA_PATH, RemoteConstant.SERVER_ACTOR_SYSTEM_NAME, address, RemoteConstant.SERVER_FRIEND_ACTOR_NAME);
        return actorSystem.actorSelection(path);
    }

    public static ActorSelection getWorkerActor(String address) {
        String path = String.format(AKKA_PATH, RemoteConstant.WORKER_ACTOR_SYSTEM_NAME, address, RemoteConstant.WORKER_ACTOR_NAME);
        return actorSystem.actorSelection(path);
    }
}
