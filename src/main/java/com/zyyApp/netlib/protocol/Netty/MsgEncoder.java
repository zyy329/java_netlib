package com.zyyApp.netlib.protocol.Netty;

import com.zyyApp.LogMgr;
import com.zyyApp.netlib.protocol.Msg.Body.Buffer_ByteBuf;
import com.zyyApp.netlib.protocol.Msg.Head.HeadType;
import com.zyyApp.netlib.protocol.Msg.Message;
import com.zyyApp.netlib.protocol.Msg.MsgTrans.MsgTransPool;
import com.zyyApp.netlib.protocol.Msg.MsgTrans.MsgTrans_Base;
import com.zyyApp.netlib.protocol.Netty.Head.HeadProc_NettyBase;
import com.zyyApp.netlib.protocol.Netty.Head.HeadProc_Netty_ICarry;
import com.zyyApp.netlib.protocol.Netty.Head.HeadProc_Netty_IdOnly;
import com.zyyApp.util.Define;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 内部通讯编码处理器;
 * 消息头只包含最简单的 消息长度 和 消息ID
 *
 * Created by zyy on 2017/4/12.
 */
public class MsgEncoder extends MessageToByteEncoder<Message> {
    private HeadProc_NettyBase headProc = null;

    public MsgEncoder(HeadType headType) {
        switch (headType) {
            case HEAD_ID_ONLY:
                headProc = new HeadProc_Netty_IdOnly();
                break;
            case HEAD_ICARRY_EXTERNAL:
                headProc = new HeadProc_Netty_ICarry();
                break;
            default:
                String errInfo = String.format("MsgEncoder; err headProc type {type:%s}", headType.toString());
                LogMgr.log.error(errInfo);
                throw new RuntimeException(errInfo);
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 消息长度, 除去存储长度本身的数据外, 剩余数据所占长度;  占位;
        out.writeInt(0);
        // 消息头占位;
        headProc.SeatHead(out);

        // 消息体;
        MsgTrans_Base trans = MsgTransPool.getInstance().getMessageTrans(msg.getMsgId());
        try {
            if (msg.getMsgObj() != null) {
                if (trans != null) {
                    // 转换写入消息信息;
                    Buffer_ByteBuf buf = Buffer_ByteBuf.poolPop();
                    buf.init(out);
                    trans.Encode(buf, msg.getMsgObj());
                    Buffer_ByteBuf.poolPush(buf);
                } else {
                    String errInfo = String.format("MsgEncoder; trans == null; {msgId:%d}", msg.getMsgId());
                    LogMgr.log.error(errInfo);
                    throw new RuntimeException(errInfo);
                }
            }

            // 计算数据长度;
            int msgLength = out.readableBytes() - Define.INT_BYTE_NUM;       // 总长度 减去 msgLength 本身的长度, 等于剩余消息数据的长度;
            out.setInt(0, msgLength);

            // 写入消息头; 从消息长度后面开始写;
            headProc.WriteHead(msg, out, Define.INT_BYTE_NUM);
        } finally {
            // 不再使用, 将消息对象还会重用池中;
            Message.poolPush(msg);
        }
    }
}
