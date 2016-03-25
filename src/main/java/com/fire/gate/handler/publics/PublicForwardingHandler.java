/**
 * 
 */
package com.fire.gate.handler.publics;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fire.gate.Config;
import com.fire.gate.NamedThreadFactory;
import com.fire.gate.ServerManager;
import com.fire.gate.UserManager;
import com.fire.gate.handler.publics.Login.C2S_Login;
import com.fire.gate.handler.publics.Login.S2C_Login;
import com.fire.gate.net.privates.PrivatePacket;
import com.fire.gate.net.publics.PublicHandler;
import com.fire.gate.net.publics.PublicPacket;
import com.fire.gate.util.HttpUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;

/**
 * 客户端与服务器通信
 * 
 * @author lhl
 *
 *         2016年3月25日 上午11:02:18
 */
public abstract class PublicForwardingHandler implements PublicHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(PublicForwardingHandler.class);
    public static final AttributeKey<Integer> KEY_UID = AttributeKey.valueOf("KEY_UID");
    public static final AttributeKey<Integer> KEY_LOGIC_SERVER_ID = AttributeKey.valueOf("KEY_LOGIC_SERVER_ID");
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
            new NamedThreadFactory("AUTHENTICATE"));

    @Override
    public void handle(Channel channel, PublicPacket packet) {
        try {
            Integer uid = channel.attr(KEY_UID).get();
            if (uid == null) {
                authenticate(channel, packet);
            } else {
                PrivatePacket privatePacket = packet.toPrivate(uid.intValue());
                forwarding(channel, privatePacket);
            }
        } catch (Throwable t) {
            LOG.error("", t);
        }
    }

    /**
     * 登录验证
     * 
     * @param channel 客户端连接
     * @param packet
     */
    public void authenticate(Channel channel, PublicPacket packet) {
        Runnable task = () -> {
            try {
                C2S_Login req = packet.toProto(C2S_Login.getDefaultInstance());
                String token = req.getToken();
                String authenticateUrl = Config.getString("LOGIN_AUTHENTICATE_URL");
                authenticateUrl = String.format(authenticateUrl, token);
                String resp = HttpUtil.GET(authenticateUrl);
                processAuthenticateResponse(resp, channel);
            } catch (Exception e) {
                LOG.error("", e);
                sendError(channel, "Server internel error", false);
            }
        };
        executor.submit(task);
    }

    /**
     * 处理登录验证应答
     * 
     * <pre>
     * resp: {"code":"0","msg":"ok","uid":"13579"}
     * </pre>
     * 
     * @param resp
     * @param channel 客户端连接
     */
    public void processAuthenticateResponse(String resp, Channel channel) {
        JSONObject jsonObj = JSON.parseObject(resp);
        if (jsonObj.containsKey("code") && jsonObj.getIntValue("code") == 0 && jsonObj.containsKey("uid")) {
            int uid = jsonObj.getIntValue("uid");
            onAuthenticateSuccess(uid, channel);
        } else {
            String errMsg = jsonObj.getString("msg");
            sendError(channel, errMsg, true);
            LOG.debug("User authenticate failed, because {}", errMsg);
        }
    }

    /**
     * 登录验证通过
     * 
     * @param uid 用户id
     * @param channel 客户端连接
     */
    public void onAuthenticateSuccess(int uid, Channel channel) {
        int logicServerId = ServerManager.getSuitableLogicServerId(uid);
        if (logicServerId == 0) {
            sendError(channel, "Logic server unavailable", false);
            LOG.warn("Can not get an available logic server for user {}", uid);
            return;
        }
        channel.attr(KEY_LOGIC_SERVER_ID).set(Integer.valueOf(logicServerId));
        channel.attr(KEY_UID).set(Integer.valueOf(uid));
        addUser(uid, channel);
    }

    /**
     * 将通过登录验证的用户加入管理器
     * 
     * @param uid
     * @param channel
     */
    public void addUser(int uid, Channel channel) {
        Channel oldChannel = UserManager.addUser(uid, channel);
        if (oldChannel != null) {
            kickout(oldChannel, channel, uid);
        }
    }

    /**
     * 顶号
     * 
     * @param oldChannel
     * @param newChannel
     * @param uid
     */
    public void kickout(Channel oldChannel, Channel newChannel, int uid) {
        LOG.debug("User {} is kick out by {}", uid, newChannel.remoteAddress());
        sendError(oldChannel, "You are kicked out by another one", true);
    }

    /**
     * 响应错误应答，应答发送成功后将会关闭连接
     * 
     * @param channel 连接
     * @param message 错误描述
     * @param closeAfterSend true表示应答发送完毕后将会关闭连接
     */
    public void sendError(Channel channel, String message, boolean closeAfterSend) {
        S2C_Login.Builder rspBuilder = S2C_Login.newBuilder();
        rspBuilder.setIsOk(false);
        rspBuilder.setMessage(message);
        PublicPacket rsp = PublicPacket.from((short) 1000, rspBuilder);
        ChannelFuture future = channel.writeAndFlush(rsp);
        if (closeAfterSend) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 转发
     * 
     * @param channel
     * @param packet
     */
    protected abstract void forwarding(Channel channel, PrivatePacket packet);
}
