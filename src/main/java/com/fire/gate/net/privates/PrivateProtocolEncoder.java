package com.fire.gate.net.privates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 服务器内部通信
 * <p>
 * 明文协议编码器
 * <p>
 * 请注意，这个类被标记为{@code Sharable}，所有child channel将共用一个实例， 所以请不要在该类中放置会被多线程修改的全局资源
 * 
 * @author lhl
 *
 */
@Sharable
public class PrivateProtocolEncoder extends MessageToByteEncoder<PrivatePacket>
{
    private static final Logger LOG = LoggerFactory.getLogger(PrivateProtocolEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, PrivatePacket msg, ByteBuf out) throws Exception {
        out.writeShort(PrivatePacket.FLAG);
        out.writeShort(msg.length);
        out.writeShort(msg.code);
        out.writeInt(msg.uid);
        if (msg.body != null) {
            out.writeBytes(msg.body);
        }

        LOG.debug("SEND: {} -> {}, {}", ctx.channel().localAddress(), ctx.channel().remoteAddress(), msg);
    }
}
