/**
 * 
 */
package com.fire.gate.net.privates;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fire.gate.Component;
import com.fire.gate.Config;
import com.fire.gate.UserManager;
import com.fire.gate.net.publics.PublicPacket;
import com.fire.gate.util.BaseUtil;
import com.fire.gate.util.ClassUtil;

import io.netty.channel.Channel;

/**
 * 服务器内部通信
 * <p>
 * 请求派发处理器，负责将网络IO传过来的请求分发给指定处理器处理
 * 
 * @author lhl
 *
 *         2016年3月25日 上午11:50:12
 */
public class PrivateDispatchHandler implements PrivateHandler, Component
{
    private static final Logger LOG = LoggerFactory.getLogger(PrivateDispatchHandler.class);
    // <指令，处理器>
    private Map<Short, PrivateHandler> handlerMap;

    @Override
    public void start() throws Exception {
        loadHandler(Config.getString("PRIVATE_HANDLER_SCAN_PACKAGES"));
        LOG.debug("PrivateDispatchHandler start");
    }

    @Override
    public void handle(Channel channel, PrivatePacket packet) {
        PrivateHandler handler = handlerMap.get(Short.valueOf(packet.code));
        if (handler != null) { // 服务器内部消息
            handler.handle(channel, packet);
        } else { // 将从内部服务器接收到的消息转发给对应的用户
            Channel userChannel = UserManager.getUser(packet.uid);
            if (userChannel != null) {
                PublicPacket destPacket = packet.toPublic();
                userChannel.writeAndFlush(destPacket);
            } else {
                LOG.warn("Lose connection with user {}, can not forwarding response packet", packet.uid);
            }
        }
    }

    /**
     * 加载指令处理器
     * 
     * @param searchPackage 搜索包名，多个包名使用逗号分割
     * @throws Exception
     */
    private void loadHandler(String searchPackage) throws Exception {
        if (BaseUtil.isNullOrEmpty(searchPackage)) {
            return;
        }

        String[] packages = BaseUtil.split(searchPackage.trim(), ",");
        for (String onePackage : packages) {
            if (!BaseUtil.isNullOrEmpty(onePackage)) {
                LOG.debug("Load handler from package {}", onePackage);
                List<Class<?>> classList = ClassUtil.getClasses(onePackage);
                for (Class<?> handler : classList) {
                    PrivateRequestHandler annotation = handler.getAnnotation(PrivateRequestHandler.class);
                    if (annotation != null) {
                        short code = annotation.code();
                        PrivateHandler handlerInstance = (PrivateHandler) handler.newInstance();
                        addHandler(code, handlerInstance);
                    }
                }
            }
        }
    }

    /**
     * 注册指令处理器
     * 
     * @param code
     * @param handler
     * @return 若该指令已注册过则返回之前注册的处理器，否则返回null
     * @throws IllegalStateException
     */
    private void addHandler(short code, PrivateHandler handler) throws IllegalStateException {
        PrivateHandler oldHandler = handlerMap.put(Short.valueOf(code), handler);
        if (oldHandler != null) {
            throw new IllegalStateException("Duplicate handler for code " + code + ", old: "
                    + oldHandler.getClass().getName() + ", new: " + handler.getClass().getName());
        }
    }

    @Override
    public void stop() throws Exception {
        LOG.debug("PrivateDispatchHandler stop");
    }
}
