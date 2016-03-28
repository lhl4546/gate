/**
 * 
 */
package com.fire.gate.net.privates;

import com.fire.gate.Config;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 服务器内部通信
 * 
 * @author lhl
 *
 *         2016年1月30日 上午9:29:24
 */
public class PrivateChannelInitializer extends ChannelInitializer<Channel>
{
    private PrivateInboundHandler netHandler;
    private ChannelOutboundHandler encoder = new PrivateProtocolEncoder();

    public PrivateChannelInitializer(PrivateDispatchHandler dispatcher) {
        netHandler = new PrivateInboundHandler(dispatcher);
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("ENCODER", encoder);
        pipeline.addLast("DECODER", new PrivateProtocolDecoder());
        pipeline.addLast("IDLE_HANDLER", new IdleStateHandler(0, Config.getInt("PRIVATE_WRITE_IDLE"), 0));
        pipeline.addLast("NET_HANDLER", netHandler);
    }
}
