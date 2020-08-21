package com.lxm.netty.server.codec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxm.netty.codec.coder.EntityDecoder;
import com.lxm.netty.codec.registry.EntityDecoderRegistry;
import com.lxm.netty.entity.RequestEntity;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 
 * ClassName: com.lxm.netty.codec.NettyRequestDecoder <br/>
 * Function: netty请求解码器，将字节缓冲的数据转换了请求模型对象 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public class NettyRequestDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(NettyRequestDecoder.class);
    

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 5) {
            int id = in.readInt();
            int type = in.readByte();
            EntityDecoder<?> dataDecoder = EntityDecoderRegistry.getDecoder(type);
            if (dataDecoder == null) {
                logger.warn("Unknown type of request data decoder: {}", type);
                return;
            }
            Object data;
            if (in.readableBytes() == 0) {
                data = null;
            } else {
                data = dataDecoder.decode(in);
            }
            RequestEntity<Object> entity = new RequestEntity<>(id, type, data);
            out.add(entity);
        }
    }
}
