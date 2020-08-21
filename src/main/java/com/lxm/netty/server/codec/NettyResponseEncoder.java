package com.lxm.netty.server.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxm.netty.codec.coder.EntityEncoder;
import com.lxm.netty.codec.registry.EntityEncoderRegistry;
import com.lxm.netty.entity.ResponseEntity;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 * ClassName: com.lxm.netty.server.codec.NettyResponseEncoder <br/>
 * Function: netty响应编码器，将响应模型对象编码写入字节缓冲中 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public class NettyResponseEncoder extends MessageToByteEncoder<ResponseEntity> {
    
    private static final Logger logger = LoggerFactory.getLogger(NettyResponseEncoder.class);
    
    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseEntity response, ByteBuf out) throws Exception {
        EntityEncoder<Object> dataEncoder = EntityEncoderRegistry.getEncoder(response.getType());
        if (dataEncoder == null) {
            logger.warn("Unknown type of response data encoder: {}", response.getType());
            return;
        }
        out.writeInt(response.getId());
        out.writeByte(response.getType());
        out.writeByte(response.getStatus());
        dataEncoder.writeTo(response.getData(), out);
    }

}
