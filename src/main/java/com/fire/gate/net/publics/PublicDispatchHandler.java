/**
 * 
 */
package com.fire.gate.net.publics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fire.gate.Component;
import com.fire.gate.Config;
import com.fire.gate.util.BaseUtil;
import com.fire.gate.util.ClassUtil;

import io.netty.channel.Channel;

/**
 * 客户端与服务器通信
 * <p>
 * 请求派发处理器，负责将网络IO传过来的请求分发给指定处理器处理
 * 
 * @author lhl
 *
 *         2016年1月30日 下午3:49:52
 */
public final class PublicDispatchHandler implements PublicHandler, Component
{
    private static final Logger LOG = LoggerFactory.getLogger(PublicDispatchHandler.class);
    // <指令类型，处理器>
    private Map<Byte, PublicHandler> handlerMap;

    public PublicDispatchHandler() {
        handlerMap = new HashMap<>();
    }

    /**
     * 该方法将在Netty I/O线程池中运行
     */
    @Override
    public void handle(Channel channel, PublicPacket packet) {
        PublicHandler handler = handlerMap.get(Byte.valueOf(packet.type));
        if (handler == null) {
            LOG.warn("No handler found for type {}, session will be closed", packet.type);
            channel.close();
            return;
        }

        handler.handle(channel, packet);
    }

    @Override
    public void start() throws Exception {
        loadHandler(Config.getString("PUBLIC_HANDLER_SCAN_PACKAGES"));
        LOG.debug("PublicDispatchHandler start");
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
                    PublicRequestHandler annotation = handler.getAnnotation(PublicRequestHandler.class);
                    if (annotation != null) {
                        byte type = annotation.type();
                        PublicHandler handlerInstance = (PublicHandler) handler.newInstance();
                        addHandler(type, handlerInstance);
                    }
                }
            }
        }
    }

    /**
     * 注册指令处理器
     * 
     * @param type
     * @param handler
     * @return 若该指令已注册过则返回之前注册的处理器，否则返回null
     * @throws IllegalStateException
     */
    private void addHandler(byte type, PublicHandler handler) throws IllegalStateException {
        PublicHandler oldHandler = handlerMap.put(Byte.valueOf(type), handler);
        if (oldHandler != null) {
            throw new IllegalStateException("Duplicate handler for type " + type + ", old: "
                    + oldHandler.getClass().getName() + ", new: " + handler.getClass().getName());
        }
    }

    @Override
    public void stop() {
        LOG.debug("PublicDispatchHandler stop");
    }
}
