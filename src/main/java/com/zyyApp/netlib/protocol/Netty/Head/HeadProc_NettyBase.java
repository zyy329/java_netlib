package com.zyyApp.netlib.protocol.Netty.Head;

import com.zyyApp.netlib.protocol.Msg.Message;
import io.netty.buffer.ByteBuf;

/**
 * 基于netty 实现的 内部通讯消息头处理器;
 * 消息头只包含简单的 消息ID 信息;
 * Created by zyy on 2017/5/6.
 */
public abstract class HeadProc_NettyBase{
    /**
     * 消息头占位操作; 有些消息头信息需要在消息体写入后才能进行运算获得; 所以先占位;
     */
    public abstract void SeatHead(ByteBuf out);

    /**
     *  在占位函数调用过后的前提下, 最后正式写入消息头信息;
     */
    public abstract void WriteHead(Message msg, ByteBuf out, int startIdx);

    /**
     * 读取消息头信息, 并进行内部有效性判断(如果有验证的话);
     * @return msgId;
     */
    public abstract int ReadHead(ByteBuf in);

    /**
     * 获得消息头长度;
     * @return msgHeadLength;
     */
    public abstract int getHeadLength();
}
