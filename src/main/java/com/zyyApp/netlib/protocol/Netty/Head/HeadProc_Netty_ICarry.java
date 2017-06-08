package com.zyyApp.netlib.protocol.Netty.Head;

import com.zyyApp.netlib.protocol.Msg.Message;
import com.zyyApp.util.Define;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于netty 实现的 iCarry, 对外通讯消息头 处理器;
 * Created by zyy on 2017/5/6.
 */
public class HeadProc_Netty_ICarry extends HeadProc_NettyBase{
    // 消息头长度;
    // 包含 校验码，消息id，发送时间（占位但是已经没有使用了）
    private static final int HEAD_LENGTH = Define.INT_BYTE_NUM * 3;
    /**已发送的消息数量,计算验证码用*/
    private AtomicInteger sendedMsgNum = new AtomicInteger(0);

    public void SeatHead(ByteBuf out) {
        // 占位
        // 校验码，消息id，发送时间（占位但是已经没有使用了）
        out.writeInt(0);
        out.writeInt(0);
        out.writeInt(0);
    }

    public void WriteHead(Message msg, ByteBuf out, int startIdx) {
        //消息长度 获得出来;
        out.markReaderIndex();
        int length = out.readInt();
        out.resetReaderIndex();

        //校验码
        int key = sendedMsgNum.getAndIncrement() ^ length;
        key = ((~key & (1 << 9)) | (key & ~(1 << 9)));

        //校验码
        out.setInt(startIdx, key);
        //消息id
        out.setInt(startIdx + Define.INT_BYTE_NUM, msg.getMsgId());
        //发送时间
        out.setInt(startIdx + Define.INT_BYTE_NUM*2, 0);
    }

    public int ReadHead(ByteBuf in) {
        // 校验码;  校验逻辑暂缺;
        int checkCode = in.readInt();
        // 消息id;
        int msgId = in.readInt();
        // 发送时间;
        int sendTime = in.readInt();
        return msgId;
    }

    public int getHeadLength() {
        return HEAD_LENGTH;
    }
}
