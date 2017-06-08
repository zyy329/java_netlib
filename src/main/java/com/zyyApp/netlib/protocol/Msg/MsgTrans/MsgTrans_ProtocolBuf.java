package com.zyyApp.netlib.protocol.Msg.MsgTrans;

import com.google.protobuf.MessageLite;
import com.zyyApp.netlib.protocol.Msg.Body.BufferBean;

/**
 * 基于 Protocol Buffer 实现的 消息转换类, 用于将 消息对象 和 byte 数组之间的互相转换;
 * Created by zyy on 2017/4/11.
 */
public class MsgTrans_ProtocolBuf<Msg extends MessageLite> extends MsgTrans_Base {
    private final Msg prototype;

    @SuppressWarnings("unchecked")
    public MsgTrans_ProtocolBuf(Msg prototype) {
        if(prototype == null) {
            throw new NullPointerException("prototype");
        } else {
            this.prototype = (Msg)prototype.getDefaultInstanceForType();
        }
    }


    /** 编码; 消息对象 转换为 byte 数组 */
    @Override
    @SuppressWarnings("unchecked")
    public void Encode(BufferBean buf, Object msg) throws Exception {
        if (msg != null) {
            //if (msg instanceof Msg) {     // 报错, 不知道原理;
            if (msg.getClass() == prototype.getClass()) {       // 判断对象类型是否符合预期;
                buf.writeBytes(((Msg)msg).toByteArray());
            } else {
                // err;
                throw new IllegalArgumentException("msg Type Err");
            }
        }
    }
    /** 解码; byte 数组 转换为 消息对象; */
    @Override
    public Object Decode(BufferBean buf, int msgLength) throws Exception {
        if (msgLength > 0) {
            byte[] msgBodyBytes = buf.readBytes(msgLength);
            return this.prototype.getParserForType().parseFrom(msgBodyBytes);
        }

        return null;
    }
}
