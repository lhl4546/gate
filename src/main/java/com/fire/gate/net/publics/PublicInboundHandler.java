/**
 * 
 */
package com.fire.gate.net.publics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 客户端与服务器通信
 * 
 * @author lhl
 *
 *         2016年1月29日 下午5:36:23
 */
@Sharable
public class PublicInboundHandler extends SimpleChannelInboundHandler<PublicPacket>
{
    private static final Logger LOG = LoggerFactory.getLogger(PublicInboundHandler.class);
    private PublicHandler dispatcher;

    public PublicInboundHandler(PublicHandler dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.debug("{}", ctx.channel().remoteAddress(), cause);
        ctx.channel().close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleEvent = (IdleStateEvent) evt;
            if (idleEvent.state() == IdleState.READER_IDLE) {
                LOG.debug("Read from {} idle, close the session", ctx.channel().remoteAddress());
                ctx.close();
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PublicPacket msg) throws Exception {
        dispatcher.handle(ctx.channel(), msg);
    }
}
