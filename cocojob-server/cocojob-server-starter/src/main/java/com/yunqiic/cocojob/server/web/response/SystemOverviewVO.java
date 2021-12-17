package com.yunqiic.cocojob.server.web.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 系统概览
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class SystemOverviewVO {

    private long jobCount;
    private long runningInstanceCount;
    private long failedInstanceCount;
    // 服务器时区
    private String timezone;
    // 服务器时间
    private String serverTime;

    private CurrentServerInfo currentServerInfo;

    @Getter
    @AllArgsConstructor
    public static class CurrentServerInfo {
        private final long id;
        private final String ip;
        private final String version;
    }
}
