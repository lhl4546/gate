/**
 * 
 */
package com.fire.gate.net.publics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 客户端与服务器通信根据类型进行转发
 * 
 * @author lhl
 *
 *         2016年3月25日 上午11:08:22
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicRequestHandler {
    /** 指令类型 */
    byte type();
}
