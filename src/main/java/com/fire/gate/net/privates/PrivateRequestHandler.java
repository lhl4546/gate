/**
 * 
 */
package com.fire.gate.net.privates;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lhl
 *
 *         2016年3月25日 上午11:54:05
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrivateRequestHandler {
    short code();
}
