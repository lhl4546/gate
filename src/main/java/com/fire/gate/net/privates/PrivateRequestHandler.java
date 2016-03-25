/**
 * 
 */
package com.fire.gate.net.privates;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务器内部通信根据指令进行转发，应用需要预留内部指令用于服务器之间传输控制消息，推荐预留0~999作为内部控制指令
 * 
 * @author lhl
 *
 *         2016年3月25日 上午11:54:05
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrivateRequestHandler {
    /** 指令 */
    short code();
}
