package com.zyyApp.netlib.protocol.Netty.Head;

import com.zyyApp.netlib.protocol.Msg.Message;
import com.zyyApp.util.simple.Define;
import io.netty.buffer.ByteBuf;

/**
 * 基于netty 实现的 HEAD_ID_ONLY 消息头处理器;
 * Created by zyy on 2017/5/6.
 */
public class HeadProc_Netty_IdOnly extends HeadProc_NettyBase{
    // 消息头长度; 只有一个 msgId;
    private static final int HEAD_LENGTH = Define.INT_BYTE_NUM;

    public void SeatHead(ByteBuf out) {
        // 占位
        out.writeInt(0);        // msgId;
    }

    public void WriteHead(Message msg, ByteBuf out, int startIdx) {
        //消息id
        out.setInt(startIdx, msg.getMsgId());
    }

    public int ReadHead(ByteBuf in) {
        //消息id
        int msgId = in.readInt();
        return msgId;
    }

    public int getHeadLength() {
        return HEAD_LENGTH;
    }
}
