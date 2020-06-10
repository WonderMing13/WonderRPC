package org.wonderming.codec.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;
import org.springframework.stereotype.Component;
import org.wonderming.entity.RpcResponse;
import org.wonderming.exception.SerializerException;
import org.wonderming.serializer.SerializerEngine;
import org.wonderming.serializer.SerializerEnum;

import java.io.Serializable;

/**
 * @author wangdeming
 * @date 2019-09-19 14:07
 **/
public class WonderRpcEncoder extends MessageToByteEncoder<Object> {

    private Class<?> genericClass;

    public WonderRpcEncoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf){
        byte[] bytes;
        if (byteBuf == null){
            return;
        }
        //没有继承序列化接口 则抛出异常
        if (!(o instanceof Serializable)){
            throw new SerializerException("Not implements Serializable...");
        }
        //编码是否为传入对象的实例
        if (genericClass.isInstance(o)){
            //序列化要传输的数据
            bytes = SerializerEngine.serialize(o, SerializerEnum.ProtoStuffSerializer);
            //先在消息头将消息长度写入
            byteBuf.writeInt(bytes.length);
            //在消息体中包含发送的数据
            byteBuf.writeBytes(bytes);
        }
    }
}
