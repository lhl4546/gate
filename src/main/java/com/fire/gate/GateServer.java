/**
 * 
 */
package com.fire.gate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fire.gate.net.privates.PrivateDispatchHandler;
import com.fire.gate.net.privates.PrivateServer;
import com.fire.gate.net.publics.PublicDispatchHandler;
import com.fire.gate.net.publics.PublicServer;
import com.fire.gate.util.HttpUtil;

/**
 * @author lhl
 *
 *         2016年3月28日 上午9:24:33
 */
public class GateServer implements Component
{
    static {
        Config.parse("app.properties");
    }

    private static final Logger LOG = LoggerFactory.getLogger(GateServer.class);
    private PrivateDispatchHandler privateDispatcher = new PrivateDispatchHandler();
    private PrivateServer privateServer = new PrivateServer(privateDispatcher);
    private PublicDispatchHandler publicDispatcher = new PublicDispatchHandler();
    private PublicServer publicServer = new PublicServer(publicDispatcher);

    public static void main(String[] args) {
        GateServer instance = new GateServer();
        instance.start();
    }

    @Override
    public void start() {
        try {
            HttpUtil.INSTANCE.start();
            privateDispatcher.start();
            privateServer.start();
            publicDispatcher.start();
            publicServer.start();
            addShutdownHook();
            LOG.debug("Gate server start successfully");
        } catch (Exception e) {
            LOG.error("Gate server start failed", e);
        }
    }

    private void addShutdownHook() {
        Runnable action = () -> {
            LOG.debug("Shutdown hook has been triggered");
            stop();
        };
        Thread hook = new Thread(action);
        Runtime.getRuntime().addShutdownHook(hook);
    }

    @Override
    public void stop() {
        try {
            publicServer.stop();
            publicDispatcher.stop();
            privateServer.stop();
            privateDispatcher.stop();
            HttpUtil.INSTANCE.stop();
            LOG.debug("Gate server stop");
        } catch (Exception e) {
            LOG.error("Gate server stop failed", e);
        }
    }
}
