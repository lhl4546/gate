package com.fire.gate.net.privates;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 服务器内部通信
 * <p>
 * 明文协议解码器
 * 
 * @author lhl
 *
 */
public class PrivateProtocolDecoder extends ByteToMessageDecoder
{
    private static final Logger LOG = LoggerFactory.getLogger(PrivateProtocolDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < PrivatePacket.HEAD_SIZE) {
            return; // 不足以构成一个完整数据包，继续读取以等待下一次解码
        }
        short flag = in.getShort(in.readerIndex());
        if (flag != PrivatePacket.FLAG) {
            in.skipBytes(2);
            return; // 标志位不匹配，丢弃这2个字节
        }
        short length = in.getShort(in.readerIndex() + 2);
        if (length < PrivatePacket.HEAD_SIZE) {
            ctx.close(); // 参数有误，放弃这个链接
            return;
        }
        if (in.readableBytes() < length) {
            return; // 不足以构成一个完整数据包，继续读取以等待下一次解码
        }

        PrivatePacket packet = new PrivatePacket(in.getShort(in.readerIndex() + 4));
        in.skipBytes(2); // flag
        packet.length = in.readShort(); // length
        in.skipBytes(2); // code
        packet.uid = in.readInt();
        int bodyLength = length - PrivatePacket.HEAD_SIZE;
        if (bodyLength > 0) {
            byte[] body = new byte[bodyLength];
            in.readBytes(body);
            packet.body = body;
        }

        out.add(packet);

        LOG.debug("RECV: {} -> {}, {}", ctx.channel().remoteAddress(), ctx.channel().localAddress(), packet);
    }
}
