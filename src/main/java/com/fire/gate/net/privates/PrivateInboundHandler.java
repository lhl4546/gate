/**
 * 
 */
package com.fire.gate.net.privates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.debug("{}", ctx.channel().remoteAddress(), cause);
        ctx.channel().close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleEvent = (IdleStateEvent) evt;
            if (idleEvent.state() == IdleState.WRITER_IDLE) {
                LOG.debug("Write to {} idle, send ping message", ctx.channel().remoteAddress());
                // TODO send ping message
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PrivatePacket msg) throws Exception {
        dispatcher.handle(ctx.channel(), msg);
    }
}
