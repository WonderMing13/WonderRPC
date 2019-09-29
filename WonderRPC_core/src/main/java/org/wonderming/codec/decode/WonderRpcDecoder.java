package org.wonderming.codec.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import org.wonderming.entity.RpcResponse;
import org.wonderming.serializer.SerializerEngine;
import org.wonderming.serializer.SerializerEnum;

/** 处理 TCP粘包问题
 * @author wangdeming
 * @date 2019-09-19 14:08
 **/
public class WonderRpcDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * maxFrameLength: 表示的是包的最大长度
     * lengthFieldOffset: 表示的是长度域的偏移量
     * lengthFieldLength: 表示记录该帧数据长度的字段本身的长度
     * lengthAdjustment: 表示该字段加长度字段等于数据帧的长度
     */
    public WonderRpcDecoder(int maxFrameLength) {
        super(maxFrameLength, 0, 4, 0, 4);
    }

    /**
     * 1.引用计数
     * 2.读取字节
     * 3.释放引用对象回归对象池
     * @param context ChannelHandlerContext
     * @param in ByteBuf
     * @return Object
     */
    @Override
    public Object decode(ChannelHandlerContext context, ByteBuf in){
        Object object = null;
        ByteBuf byteBuf = null;
        //int类型占4位 头长度的字节数
        int headLength = 4;
        //可读字节小于头部长度 不符合
        if (in.readableBytes() < headLength){
            return object;
        }
        try {
            //调用LengthFieldBasedFrameDecoder处理粘包问题
            Object decode = super.decode(context,in);
            byteBuf = (ByteBuf) decode;
            //索引的标记
            byteBuf.markReaderIndex();
            if (in.readableBytes() < byteBuf.readInt()){
                byteBuf.resetReaderIndex();
                return object;
            }
            //创建data长度的字节数组
            byte[] bytes = new byte[byteBuf.readInt()];
            //ByteBuf缓冲区写入字节数组中
            byteBuf.readBytes(bytes);
            //反序列化字节
            object = SerializerEngine.deserialize(bytes, RpcResponse.class,SerializerEnum.JsonSerializer);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //释放引用对象 GC回收
            ReferenceCountUtil.release(byteBuf);
        }
        return object;
    }
}
