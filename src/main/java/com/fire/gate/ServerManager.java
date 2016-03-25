/**
 * 
 */
package com.fire.gate;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.Channel;

/**
 * @author lhl
 *
 *         2016年3月25日 上午9:35:38
 */
public class ServerManager
{
    // <serverid, server connection>
    private static Map<Integer, Channel> logicServer = new HashMap<>();

    public static void addLogicServer(int serverId, Channel channel) {
        logicServer.put(Integer.valueOf(serverId), channel);
    }

    public static Channel getLogicServer(int serverId) {
        return logicServer.get(Integer.valueOf(serverId));
    }

    public static int getSuitableLogicServerId(int uid) {
        if (logicServer.isEmpty()) {
            return 0;
        }

        return uid % logicServer.size();
    }
}
