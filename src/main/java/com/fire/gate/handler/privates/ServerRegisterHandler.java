/**
 * 
 */
package com.fire.gate.handler.privates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fire.gate.ServerManager;
import com.fire.gate.handler.privates.ServerRegister.C2S_ServerRegister;
import com.fire.gate.net.privates.PrivateHandler;
import com.fire.gate.net.privates.PrivatePacket;
import com.fire.gate.net.privates.PrivateRequestHandler;

import io.netty.channel.Channel;

/**
 * 服务器注册
 * 
 * @author lhl
 *
 *         2016年3月25日 上午11:47:57
 */
@PrivateRequestHandler(code = 100)
public class ServerRegisterHandler implements PrivateHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(ServerRegister.class);

    @Override
    public void handle(Channel channel, PrivatePacket packet) {
        C2S_ServerRegister req = packet.toProto(C2S_ServerRegister.getDefaultInstance());
        int serverId = req.getServerId();
        int serverType = req.getServerType();
        if (serverType == 1) {
            ServerManager.addLogicServer(serverId, channel);
        }

        LOG.debug("Server type {}, id {} registered {}", serverType, serverId, channel.remoteAddress());
    }
}
