package org.wonderming.serializer.serializerimpl;

import org.wonderming.serializer.ISerializer;

import java.io.*;

/**
 * JDK的序列化和反序列化
 * @author wangdeming
 * @date 2019-09-17 15:56
 **/
public class JavaSerializer implements ISerializer {

    @Override
    public <T> byte[] serialize(T obj) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        T obj;
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        try {
            final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            obj = (T)objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
        return obj;
    }
}
