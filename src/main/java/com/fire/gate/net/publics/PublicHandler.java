/**
 * 
 */
package com.fire.gate.net.publics;

import io.netty.channel.Channel;

/**
 * 客户端与服务器通信
 * 
 * @author lhl
 *
 *         2016年3月25日 上午11:01:35
 */
public interface PublicHandler
{
    void handle(Channel channel, PublicPacket packet);
}
