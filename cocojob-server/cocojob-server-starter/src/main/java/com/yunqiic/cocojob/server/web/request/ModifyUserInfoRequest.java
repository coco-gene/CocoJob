package com.yunqiic.cocojob.server.web.request;

import lombok.Data;

/**
 * 创建/修改 UserInfo 请求
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class ModifyUserInfoRequest {

    private Long id;

    private String username;
    private String password;
    private String webHook;

    // 手机号
    private String phone;
    // 邮箱地址
    private String email;
}
