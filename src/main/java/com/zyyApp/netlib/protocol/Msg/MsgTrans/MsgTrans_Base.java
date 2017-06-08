package com.zyyApp.netlib.protocol.Msg.MsgTrans;

import com.zyyApp.netlib.protocol.Msg.Body.BufferBean;

/**
 * 消息转换基类, 用于将 消息对象 和 byte 数组之间的互相转换;
 * Created by zyy on 2017/4/12.
 */
public abstract class MsgTrans_Base {
    public int msgId;                   // 消息ID;


    /** 编码; 消息对象 转换为 byte 数组 */
    public abstract void Encode(BufferBean buf, Object msg) throws Exception;
    /** 解码; byte 数组 转换为 消息对象; */
    public abstract Object Decode(BufferBean buf, int msgLength) throws Exception;
}
