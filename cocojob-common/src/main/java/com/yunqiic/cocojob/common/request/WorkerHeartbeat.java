package com.yunqiic.cocojob.common.request;

import com.yunqiic.cocojob.common.PowerSerializable;
import com.yunqiic.cocojob.common.model.DeployedContainerInfo;
import com.yunqiic.cocojob.common.model.SystemMetrics;
import lombok.Data;

import java.util.List;


/**
 * Worker 上报健康信息（worker定时发送的heartbeat）
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class WorkerHeartbeat implements PowerSerializable {

    // 本机地址 -> IP:port
    private String workerAddress;
    // 当前 appName
    private String appName;
    // 当前 appId
    private Long appId;
    // 当前时间
    private long heartbeatTime;
    // 当前加载的容器（容器名称 -> 容器版本）
    private List<DeployedContainerInfo> containerInfos;
    // worker 版本信息
    private String version;
    // 使用的通讯协议 AKKA / HTTP
    private String protocol;
    // worker tag，标识同一个 worker 下的一类集群 ISSUE: 226
    private String tag;
    // 客户端名称
    private String client;
    // 扩展字段
    private String extra;

    private SystemMetrics systemMetrics;
}
