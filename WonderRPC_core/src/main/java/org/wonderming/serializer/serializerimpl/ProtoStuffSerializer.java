package org.wonderming.serializer.serializerimpl;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;
import org.wonderming.serializer.ISerializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangdeming
 * @date 2019-09-17 15:57
 **/
public class ProtoStuffSerializer implements ISerializer {

    /**
     * 缓存类对象与Schema的对应关系
     */
    private static final Map<Class<?>, Schema<?>> SCHEMA_MAP = new ConcurrentHashMap<>();

    /**
     * 用于高效便捷生成类实例，无需构造方法支持
     */
    private static final Objenesis OBJENESIS = new ObjenesisStd(true);

    @SuppressWarnings("unchecked")
    private <T> Schema<T> getSchema(Class<T> tClass){
        return (Schema<T>)SCHEMA_MAP.computeIfAbsent(tClass,RuntimeSchema::createFrom);
    }

    /**
     * 对象 -> 字节数组
     * @param obj 序列化对象
     * @param <T> 原始对象类型
     * @return 字节数组
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try{
            final Schema<T> schema = getSchema(cls);
            return ProtobufIOUtil.toByteArray(obj,schema,buffer);
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage(),e);
        }finally {
            buffer.clear();
        }
    }

    /**
     * 字节数组->对象
     * @param data 序列化字节数组
     * @param clazz 原始类型的类对象
     * @param <T> 原始对象的类型
     * @return Type
     */
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try{
            final T message = OBJENESIS.newInstance(clazz);
            final Schema<T> schema = getSchema(clazz);
            ProtobufIOUtil.mergeFrom(data,message,schema);
            return message;
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage(),e);
        }
    }
}
