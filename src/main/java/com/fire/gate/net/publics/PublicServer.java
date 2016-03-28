/**
 * 
 */
package com.fire.gate.net.publics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fire.gate.Component;
import com.fire.gate.Config;
import com.fire.gate.NamedThreadFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 客户端与服务器通信
 * <p>
 * 基于Netty实现的网络服务器
 * 
 * @author lhl
 *
 *         2016年1月29日 下午6:16:21
 */
public class PublicServer implements Component
{
    private static final Logger LOG = LoggerFactory.getLogger(PublicServer.class);
    private ServerBootstrap bootstrap;
    private EventLoopGroup bossgroup;
    private EventLoopGroup childgroup;
    private Channel serverSocket;
    private int port;
    private PublicDispatchHandler dispatcher;

    /**
     * @param dispatcher 消息派发处理器
     */
    public PublicServer(PublicDispatchHandler dispatcher) {
        this.port = Config.getInt("PUBLIC_PORT");
        this.bootstrap = new ServerBootstrap();
        this.bossgroup = new NioEventLoopGroup(1, new NamedThreadFactory("ACCEPTOR"));
        int netiothreads = Runtime.getRuntime().availableProcessors();
        this.childgroup = new NioEventLoopGroup(netiothreads, new NamedThreadFactory("PUBLIC_NET_IO"));
        this.dispatcher = dispatcher;
    }

    @Override
    public void start() throws Exception {
        bootstrap.group(bossgroup, childgroup).channel(NioServerSocketChannel.class).childHandler(getInitializer())
                .option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_LINGER, 0)
                .childOption(ChannelOption.TCP_NODELAY, true);
        ChannelFuture future = bootstrap.bind(port);
        serverSocket = future.sync().channel();
        LOG.debug("PublicServer start listen on port {}", port);
    }

    private ChannelInitializer<Channel> getInitializer() {
        return new PublicChannelInitializer(dispatcher);
    }

    @Override
    public void stop() {
        if (serverSocket != null) {
            serverSocket.close();
        }
        if (bossgroup != null) {
            bossgroup.shutdownGracefully();
        }
        if (childgroup != null) {
            childgroup.shutdownGracefully();
        }
        LOG.debug("PublicServer stop");
    }
}
