package com.yunqiic.cocojob.common.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunqiic.cocojob.common.exception.CocoJobException;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * JSON工具类
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    public static String toJSONString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        }catch (Exception ignore) {
        }
        return null;
    }

    public static String toJSONStringUnsafe(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        }catch (Exception e) {
            throw new CocoJobException(e);
        }
    }

    public static byte[] toBytes(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        }catch (Exception ignore) {
        }
        return null;
    }

    public static <T> T parseObject(String json, Class<T> clz) throws JsonProcessingException {
        return objectMapper.readValue(json, clz);
    }

    public static <T> T parseObject(byte[] b, Class<T> clz) throws Exception {
        return objectMapper.readValue(b, clz);
    }

    public static <T> T parseObject(byte[] b, TypeReference<T> typeReference) throws Exception {
        return objectMapper.readValue(b, typeReference);
    }

    public static <T> T parseObjectUnsafe(String json, Class<T> clz) {
        try {
            return objectMapper.readValue(json, clz);
        }catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
        throw new CocoJobException("impossible");
    }
}
