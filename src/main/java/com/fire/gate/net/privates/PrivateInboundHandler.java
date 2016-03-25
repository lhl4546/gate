/**
 * 
 */
package com.fire.gate.net.privates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 服务器内部通信
 * 
 * @author lhl
 *
 *         2016年1月29日 下午5:36:23
 */
@Sharable
public class PrivateInboundHandler extends SimpleChannelInboundHandler<PrivatePacket>
{
    private static final Logger LOG = LoggerFactory.getLogger(PrivateInboundHandler.class);
    private PrivateHandler dispatcher;

    public PrivateInboundHandler(PrivateHandler dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.debug("{}", ctx.channel().remoteAddress(), cause);
        ctx.channel().close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PrivatePacket msg) throws Exception {
        dispatcher.handle(ctx.channel(), msg);
    }
}
