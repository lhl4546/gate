/**
 * 
 */
package com.fire.gate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.netty.channel.Channel;

/**
 * @author lhl
 *
 *         2016年3月25日 上午10:48:46
 */
public class UserManager
{
    // <uid, user connection>
    private static ConcurrentMap<Integer, Channel> userChannel = new ConcurrentHashMap<>();

    /**
     * 添加一个新玩家，并返回这个玩家的旧连接(如果有的话)，返回旧连接不为空说明顶号了
     * 
     * @param uid
     * @param channel
     * @return
     */
    public static Channel addUser(int uid, Channel channel) {
        return userChannel.put(Integer.valueOf(uid), channel);
    }

    /**
     * 查询玩家的连接
     * 
     * @param uid
     * @return
     */
    public static Channel getUser(int uid) {
        return userChannel.get(Integer.valueOf(uid));
    }
}
