package com.yunqiic.cocojob.official.processors.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Some dangerous processors must be passed in the specified JVM startup parameters to be enabled
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class SecurityUtils {

    public static final String ENABLE_FILE_CLEANUP_PROCESSOR = "cocojob.official-processor.file-cleanup.enable";

    public static final String ENABLE_DYNAMIC_SQL_PROCESSOR = "cocojob.official-processor.dynamic-datasource.enable";

    public static boolean disable(String dKey) {
        if (StringUtils.isEmpty(dKey)) {
            return false;
        }
        String property = System.getProperty(dKey);
        return !StringUtils.equals(Boolean.TRUE.toString(), property);
    }
}
