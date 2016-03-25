/**
 * 
 */
package com.fire.gate.handler.publics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fire.gate.PacketType;
import com.fire.gate.ServerManager;
import com.fire.gate.net.privates.PrivatePacket;
import com.fire.gate.net.publics.PublicRequestHandler;

import io.netty.channel.Channel;

/**
 * 转发逻辑服务器
 * 
 * @author lhl
 *
 *         2016年3月25日 上午11:32:15
 */
@PublicRequestHandler(type = PacketType.LOGIC_SERVER)
public class LogicServerForwardingHandler extends PublicForwardingHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(LogicServerForwardingHandler.class);

    @Override
    protected void forwarding(Channel channel, PrivatePacket packet) {
        Integer serverId = channel.attr(KEY_LOGIC_SERVER_ID).get();
        Channel server = ServerManager.getLogicServer(serverId);
        server.writeAndFlush(packet);
        LOG.debug("Forwarding packet {} to logic server {}", packet, serverId);
    }
}
