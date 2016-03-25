/**
 * 
 */
package com.fire.gate.net.privates;

import io.netty.channel.Channel;

/**
 * @author lhl
 *
 * 2016年3月25日 上午11:46:58
 */
public interface PrivateHandler
{
    void handle(Channel channel, PrivatePacket packet);
}
