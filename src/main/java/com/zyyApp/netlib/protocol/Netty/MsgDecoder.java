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
import com.zyyApp.util.simple.Define;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 内部通讯解码处理器;
 * 消息头只包含最简单的 消息长度 和 消息ID
 *
 * Created by zyy on 2017/4/12.
 */
public class MsgDecoder extends ByteToMessageDecoder {
    private HeadProc_NettyBase headProc = null;

    public MsgDecoder(HeadType headType) {
        switch (headType) {
            case HEAD_ID_ONLY:
                headProc = new HeadProc_Netty_IdOnly();
                break;
            case HEAD_ICARRY_EXTERNAL:
                headProc = new HeadProc_Netty_ICarry();
                break;
            default:
                String errInfo = String.format("MsgDecoder; err headProc type {type:%s}", headType.toString());
                LogMgr.log.error(errInfo);
                throw new RuntimeException(errInfo);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readable = in.readableBytes();
        if (readable < Define.INT_BYTE_NUM) {       // 确保数据长度获得;
            return;
        }
        in.markReaderIndex();                       // 我们标记一下当前的readIndex的位置
        // 读取传送过来的消息数据长度。ByteBuf 的readInt()方法会让他的readIndex增加4
        int msgLength = in.readInt();
        // 读到的长度如果小于我们传送过来的长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
        if (readable < msgLength) {
            in.resetReaderIndex();
            return;
        }

        // 传输正常, 开始解析;
        int msgBodyLength = msgLength - headProc.getHeadLength();
        // 消息头;
        ByteBuf headBuf = in.slice(in.readerIndex(), headProc.getHeadLength());
        in.readerIndex(in.readerIndex() + headProc.getHeadLength());        // 标记已读取;
        // 消息体;
        ByteBuf bodyBuf = msgBodyLength > 0 ? in.slice(in.readerIndex(), msgBodyLength) : null;
        in.readerIndex(in.readerIndex() + msgBodyLength);                   // 标记已读取;

        // 消息头处理;
        int msgId = headProc.ReadHead(headBuf);

        // 消息体处理, 生成 Message 对象;
        Message msg = Message.poolPop();
        if (bodyBuf != null) {
            MsgTrans_Base trans = MsgTransPool.getInstance().getMessageTrans(msgId);
            if (trans != null) {
                // 转换;
                Buffer_ByteBuf buf = null;
                try {
                    buf = Buffer_ByteBuf.poolPop();
                    buf.init(bodyBuf);
                    msg.init(trans.Decode(buf, msgBodyLength));
                } catch (Exception e) {
                    // 不抛出异常, 有可能是对端恶意发送的错误消息; 进行打印并断开连接即可;
                    LogMgr.log.error("MsgDecoder; trans.Decode err; {msgId:{}}:", msgId, e);
                    ctx.close();
                    return;
                } finally {
                    Buffer_ByteBuf.poolPush(buf);
                }
            } else {
                String errInfo = String.format("MsgDecoder; trans == null; {msgId:%d}", msg.getMsgId());
                LogMgr.log.error(errInfo);
                // 不抛出异常, 有可能是对端恶意发送的错误消息; 进行打印并断开连接即可;
                //throw new RuntimeException(errInfo);
                ctx.close();
                return;
            }
        }

        out.add(msg);
    }
}
