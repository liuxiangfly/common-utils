package com.lxm.netty.client.codec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxm.netty.codec.coder.EntityDecoder;
import com.lxm.netty.codec.registry.EntityDecoderRegistry;
import com.lxm.netty.entity.ResponseEntity;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 
 * ClassName: com.lxm.netty.client.codec.NettyResponseDecoder <br/>
 * Function: netty客户端响应解码器，将字节缓冲的数据转换了响应模型对象 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public class NettyResponseDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(NettyResponseDecoder.class);
    

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 6) {
            int id = in.readInt();
            int type = in.readByte();
            int status = in.readByte();
            EntityDecoder<?> dataDecoder = EntityDecoderRegistry.getDecoder(type);
            if (dataDecoder == null) {
                logger.warn("Unknown type of response data decoder: {}", type);
                return;
            }
            Object data;
            if (in.readableBytes() == 0) {
                data = null;
            } else {
                data = dataDecoder.decode(in);
            }
            ResponseEntity<Object> entity = new ResponseEntity<>(id, type, status, data);
            out.add(entity);
        }
    }
}
