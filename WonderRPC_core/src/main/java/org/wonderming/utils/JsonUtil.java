package org.wonderming.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * 根据Jakson工具类来写的
 * @author wangdeming
 * @date 2019-09-16 16:11
 **/
public class JsonUtil {
    private static ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Object转Json字符串
     * @param obj Object
     * @return String
     * @throws JsonProcessingException 异常
     */
    public static String obj2Json(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }

    /**
     * Json字符串转指定实体类
     * @param jsonStr String
     * @param beanType Class类型
     * @param <T> 实体类范型
     * @return T
     * @throws IOException IO异常
     */
    public static <T> T json2Obj(String jsonStr,Class<T> beanType) throws IOException {
        return MAPPER.readValue(jsonStr,beanType);
    }
}
