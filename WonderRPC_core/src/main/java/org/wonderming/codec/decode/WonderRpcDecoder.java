package org.wonderming.codec.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import org.wonderming.codec.encode.WonderRpcEncoder;
import org.wonderming.entity.RpcRequest;
import org.wonderming.entity.RpcResponse;
import org.wonderming.serializer.SerializerEngine;
import org.wonderming.serializer.SerializerEnum;

import java.util.List;

/** 处理 TCP粘包问题
 * @author wangdeming
 * @date 2019-09-19 14:08
 **/
public class WonderRpcDecoder extends ByteToMessageDecoder {
    private static final int HEAD_LENGTH = 4;

    private Class<?> genericClass;

    public WonderRpcDecoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes() < HEAD_LENGTH){
            return;
        }
        byteBuf.markReaderIndex();
        final int dataLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < dataLength){
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        Object obj = SerializerEngine.deserialize(data, genericClass,SerializerEnum.JavaSerializer);
        list.add(obj);
    }
}
