/**
 * 
 */
package com.fire.gate.net.privates;

import com.fire.gate.net.publics.PublicPacket;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 
 * <p>
 * 网络协议结构<br>
 * ++++++++++++++++++++++++<br>
 * 分隔符 + 包长 + 指令 + 用户id + 包体<br>
 * short + short+short+short + long + byte[]<br>
 * ++++++++++++++++++++++++<br>
 * 
 * @author Administrator
 *
 */
public class PrivatePacket
{
    public static final short HEAD_SIZE = 10;
    public static final short FLAG = 9527;
    public short length; // 包长
    public short code; // 指令
    public int uid; // 用户id
    public byte[] body; // 包体

    public PrivatePacket(short code) {
        this.code = code;
    }

    public PublicPacket toPublic() {
        PublicPacket dest = new PublicPacket(code);
        dest.body = body;
        dest.length = (short) (length - 3);
        return dest;
    }

    /**
     * 将Packet转换为Protobuf，<b>当packet包体为空时不要调用此方法</b>
     * 
     * @param pkt Packet对象
     * @param t 目标Protobuf对象
     * @return proto
     * @throws IllegalStateException
     */
    @SuppressWarnings("unchecked")
    public <T extends GeneratedMessage> T toProto(T t) throws IllegalStateException {
        try {
            return (T) t.newBuilderForType().mergeFrom(body).build();
        } catch (Exception e) {
            throw new IllegalStateException("Packet转Pb时出错:" + toString(), e);
        }
    }

    /**
     * 利用Protobuf构建Packet
     * 
     * @param code 协议号
     * @param builder 当需要构建空负载包(包体为空)时请将该参数传入null
     * @return packet
     */
    public static PublicPacket from(short code, Builder<?> builder) {
        PublicPacket packet = new PublicPacket(code);
        if (builder != null) {
            packet.body = builder.build().toByteArray();
            packet.length += packet.body.length;
        }
        packet.length += HEAD_SIZE;
        return packet;
    }

    @Override
    public String toString() {
        return "Packet: [code = " + code + ", length = " + length + ", uid =  " + uid + "]";
    }
}
