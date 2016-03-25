/**
 * 
 */
package com.fire.gate.net.privates;

import io.netty.channel.Channel;

/**
 * 服务器内部通信
 * 
 * @author lhl
 *
 *         2016年3月25日 上午11:46:58
 */
public interface PrivateHandler
{
    /**
     * @param channel 服务器内部连接
     * @param packet 服务器通信数据
     */
    void handle(Channel channel, PrivatePacket packet);
}
