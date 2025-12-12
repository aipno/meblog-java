package cn.iswxl.meblog.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 艾普诺
 * @url me.iswxl.cn
 * @date 2025-04-29
 * @description JSON 工具类
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper INSTANCE = new ObjectMapper();

    public static String toJsonString(Object obj) {
        try {
            return INSTANCE.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败: {}", e.getMessage(), e);
            return obj.toString();
        }
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return INSTANCE.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化失败: {}", e.getMessage(), e);
            return null;
        }
    }

    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        try {
            return INSTANCE.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化失败: {}", e.getMessage(), e);
            return null;
        }
    }
}
