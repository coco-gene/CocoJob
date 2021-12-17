package com.yunqiic.cocojob.server.web.request;

import com.yunqiic.cocojob.common.exception.CocoJobException;
import com.yunqiic.cocojob.common.utils.CommonUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 修改应用信息请求
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class ModifyAppInfoRequest {

    private Long id;
    private String oldPassword;
    private String appName;
    private String password;

    public void valid() {
        CommonUtils.requireNonNull(appName, "appName can't be empty");
        if (StringUtils.containsWhitespace(appName)) {
            throw new CocoJobException("appName can't contains white space!");
        }
    }
}
