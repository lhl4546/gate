/**
 * 
 */
package com.fire.gate.net.privates;

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
 * 基于Netty实现的网络服务器
 * 
 * @author lhl
 *
 *         2016年1月29日 下午6:16:21
 */
public class PrivateServer implements Component
{
    private static final Logger LOG = LoggerFactory.getLogger(PrivateServer.class);
    private ServerBootstrap bootstrap;
    private EventLoopGroup bossgroup;
    private EventLoopGroup childgroup;
    private Channel serverSocket;
    private int port;
    private PrivateDispatchHandler dispatcher;

    /**
     * @param dispatcher 消息派发处理器
     */
    public PrivateServer(PrivateDispatchHandler dispatcher) {
        this.port = Config.getInt("PRIVATE_PORT");
        this.bootstrap = new ServerBootstrap();
        this.bossgroup = new NioEventLoopGroup(1, new NamedThreadFactory("ACCEPTOR"));
        int netiothreads = Runtime.getRuntime().availableProcessors();
        this.childgroup = new NioEventLoopGroup(netiothreads, new NamedThreadFactory("NET_IO"));
        this.dispatcher = dispatcher;
    }

    @Override
    public void start() throws Exception {
        bootstrap.group(bossgroup, childgroup).channel(NioServerSocketChannel.class).childHandler(getInitializer())
                .option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_LINGER, 0)
                .childOption(ChannelOption.TCP_NODELAY, true);
        ChannelFuture future = bootstrap.bind(port);
        serverSocket = future.sync().channel();
        LOG.debug("PrivateServer start listen on port {}", port);
    }

    private ChannelInitializer<Channel> getInitializer() {
        return new PrivateChannelInitializer(dispatcher);
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
        LOG.debug("PrivateServer stop");
    }
}
