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
    private static ConcurrentMap<Integer, Channel> userChannel = new ConcurrentHashMap<>();

    public static Channel addUser(int uid, Channel channel) {
        return userChannel.put(Integer.valueOf(uid), channel);
    }

    public static Channel getUser(int uid) {
        return userChannel.get(Integer.valueOf(uid));
    }
}
