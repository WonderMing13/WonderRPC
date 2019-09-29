package org.wonderming.serializer;

import com.google.common.collect.Maps;
import org.wonderming.serializer.serializerimpl.JavaSerializer;
import org.wonderming.serializer.serializerimpl.JsonSerializer;
import org.wonderming.serializer.serializerimpl.ProtoStuffSerializer;

import java.util.Map;

/**
 * @author wangdeming
 * @date 2019-09-19 15:14
 **/
public class SerializerEngine {

    private static final Map<SerializerEnum,ISerializer> SERIALIZER_MAP = Maps.newConcurrentMap();

    static {
        SERIALIZER_MAP.put(SerializerEnum.JavaSerializer,new JavaSerializer());
        SERIALIZER_MAP.put(SerializerEnum.JsonSerializer,new JsonSerializer());
        SERIALIZER_MAP.put(SerializerEnum.ProtoStuffSerializer,new ProtoStuffSerializer());
    }

    public static <T> byte[] serialize(T obj,SerializerEnum serializerEnum){
        final ISerializer iSerializer = SERIALIZER_MAP.get(serializerEnum);
        return iSerializer.serialize(obj);
    }

    public static <T> T deserialize(byte[] data,Class<T> tClass,SerializerEnum serializerEnum){
        final ISerializer iSerializer = SERIALIZER_MAP.get(serializerEnum);
        return iSerializer.deserialize(data,tClass);
    }

}
