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

    /**
     * 添加一个逻辑服务器
     * 
     * @param serverId
     * @param channel
     */
    public static void addLogicServer(int serverId, Channel channel) {
        logicServer.put(Integer.valueOf(serverId), channel);
    }

    /**
     * 查询逻辑服务器
     * 
     * @param serverId
     * @return
     */
    public static Channel getLogicServer(int serverId) {
        return logicServer.get(Integer.valueOf(serverId));
    }

    /**
     * 返回一个逻辑服务器
     * 
     * @param uid
     * @return
     */
    public static int getSuitableLogicServerId(int uid) {
        if (logicServer.isEmpty()) {
            return 0;
        }

        return uid % logicServer.size();
    }
}
