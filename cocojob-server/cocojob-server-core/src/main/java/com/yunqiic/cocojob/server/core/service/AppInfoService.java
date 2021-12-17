package com.yunqiic.cocojob.server.core.service;

import com.yunqiic.cocojob.common.exception.CocoJobException;
import com.yunqiic.cocojob.server.persistence.remote.model.AppInfoDO;
import com.yunqiic.cocojob.server.persistence.remote.repository.AppInfoRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 应用信息服务
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Service
public class AppInfoService {

    @Resource
    private AppInfoRepository appInfoRepository;

    /**
     * 验证应用访问权限
     * @param appName 应用名称
     * @param password 密码
     * @return 应用ID
     */
    public Long assertApp(String appName, String password) {

        AppInfoDO appInfo = appInfoRepository.findByAppName(appName).orElseThrow(() -> new CocoJobException("can't find appInfo by appName: " + appName));
        if (Objects.equals(appInfo.getPassword(), password)) {
            return appInfo.getId();
        }
        throw new CocoJobException("password error!");
    }

}
