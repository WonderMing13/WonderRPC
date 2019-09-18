package org.wonderming.serializer;

/**
 * @author wangdeming
 * @date 2019-09-17 16:17
 **/
public interface ISerializer {

    /**
     * 序列化
     * @param obj 序列化对象
     * @param <T> 序列化对象类型
     * @return byte数组
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     * @param data 序列化字节数组
     * @param clazz 原始类型的类对象
     * @param <T> 对象初始类型
     * @return Type
     */
    <T> T deserialize(byte[] data,Class<T> clazz);
}
