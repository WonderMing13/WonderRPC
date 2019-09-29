package org.wonderming.serializer;

/**
 * @author wangdeming
 * @date 2019-09-19 14:40
 **/
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

    private int code;

    SerializerEnum(int code) {
        this.code = code;
    }

    public static SerializerEnum searchByCode(int code){
        for (SerializerEnum serializerEnum : values()) {
            if (serializerEnum.getCode() == code){
                return serializerEnum;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
