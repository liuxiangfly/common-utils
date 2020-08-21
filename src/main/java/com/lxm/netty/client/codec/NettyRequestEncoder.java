package com.lxm.netty.client.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxm.netty.codec.coder.EntityEncoder;
import com.lxm.netty.codec.registry.EntityEncoderRegistry;
import com.lxm.netty.entity.RequestEntity;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 * ClassName: com.lxm.netty.client.codec.NettyRequestEncoder <br/>
 * Function: netty客户端请求编码器，将请求模型对象编码写入字节缓冲中 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public class NettyRequestEncoder extends MessageToByteEncoder<RequestEntity> {
    
    private static final Logger logger = LoggerFactory.getLogger(NettyRequestEncoder.class);
    
    @Override
    protected void encode(ChannelHandlerContext ctx, RequestEntity request, ByteBuf out) throws Exception {
        EntityEncoder<Object> dataEncoder = EntityEncoderRegistry.getEncoder(request.getType());
        if (dataEncoder == null) {
            logger.warn("Unknown type of request data encoder: {}", request.getType());
            return;
        }
        out.writeInt(request.getId());
        out.writeByte(request.getType());
        dataEncoder.writeTo(request.getData(), out);
    }

}
