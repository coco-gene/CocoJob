package com.yunqiic.cocojob.server.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

/**
 * 加载配置文件
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
public class PropertyUtils {

    private static final Properties PROPERTIES = new Properties();

    public static Properties getProperties() {
        return PROPERTIES;
    }

    public static void init() {
        URL propertiesURL =PropertyUtils.class.getClassLoader().getResource("application.properties");
        Objects.requireNonNull(propertiesURL);
        try (InputStream is = propertiesURL.openStream()) {
            PROPERTIES.load(is);
        }catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }
}
