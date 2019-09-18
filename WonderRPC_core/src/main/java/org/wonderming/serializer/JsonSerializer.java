package org.wonderming.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author wangdeming
 * @date 2019-09-17 15:55
 **/
public class JsonSerializer implements ISerializer{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        //允许出现未定义处理方法(没有对应的setter方法或其他的处理器)的未知字段
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    @Override
    public <T> byte[] serialize(T obj) {
        try {
            final String json = OBJECT_MAPPER.writeValueAsString(obj);
            return json.getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        T obj;
        try {
            obj = OBJECT_MAPPER.readValue(data,clazz);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
        return obj;
    }
}
