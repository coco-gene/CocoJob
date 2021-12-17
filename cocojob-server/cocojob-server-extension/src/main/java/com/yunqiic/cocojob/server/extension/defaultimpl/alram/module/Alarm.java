package com.yunqiic.cocojob.server.extension.defaultimpl.alram.module;

import com.alibaba.fastjson.JSONObject;
import com.yunqiic.cocojob.common.OmsConstant;
import com.yunqiic.cocojob.common.PowerSerializable;
import com.yunqiic.cocojob.common.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 报警内容
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public interface Alarm extends PowerSerializable {

    String fetchTitle();

    default String fetchContent() {
        StringBuilder sb = new StringBuilder();
        JSONObject content = JSONObject.parseObject(JSONObject.toJSONString(this));
        content.forEach((key, originWord) -> {
            sb.append(key).append(": ");
            String word = String.valueOf(originWord);
            if (StringUtils.endsWithIgnoreCase(key, "time") || StringUtils.endsWithIgnoreCase(key, "date")) {
                try {
                    if (originWord instanceof Long) {
                        word = CommonUtils.formatTime((Long) originWord);
                    }
                }catch (Exception ignore) {
                }
            }
            sb.append(word).append(OmsConstant.LINE_SEPARATOR);
        });
        return sb.toString();
    }
}
