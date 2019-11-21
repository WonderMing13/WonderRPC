package org.wonderming.serializer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wangdeming
 * @date 2019-09-19 14:40
 **/
@AllArgsConstructor
public enum SerializerEnum {
    /**
     *  JDK序列化和反序列化
     */
    JavaSerializer(0),

    /**
     *  Json序列化和反序列化
     */
    JsonSerializer(1),

    /**
     *  ProtoStuff序列化和反序列化
     */
    ProtoStuffSerializer(2);

    @Getter
    private int code;
}
