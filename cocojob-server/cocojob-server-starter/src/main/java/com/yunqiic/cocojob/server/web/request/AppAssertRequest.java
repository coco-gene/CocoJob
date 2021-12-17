package com.yunqiic.cocojob.server.web.request;

import lombok.Data;

/**
 * 验证应用（应用登陆）
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class AppAssertRequest {
    private String appName;
    private String password;
}
