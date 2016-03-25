/**
 * 
 */
package com.fire.gate.net.publics;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;

/**
 * 客户端与服务器通信
 * 
 * @author lhl
 *
 *         2016年1月30日 上午9:29:24
 */
public class PublicChannelInitializer extends ChannelInitializer<Channel>
{
    private PublicInboundHandler netHandler;
    private ChannelOutboundHandler encoder = new PublicProtocolEncoder();

    public PublicChannelInitializer(PublicDispatchHandler dispatcher) {
        this.netHandler = new PublicInboundHandler(dispatcher);
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("ENCODER", encoder);
        pipeline.addLast("DECODER", new PublicProtocolDecoder());
        pipeline.addLast("NET_HANDLER", netHandler);
    }
}
