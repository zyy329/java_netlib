package com.zyyApp.netlib.client;

import com.zyyApp.netlib.protocol.Msg.Head.HeadType;
import com.zyyApp.netlib.protocol.Msg.Message;

/**
 * 网络库封装基类
 * Created by zyy on 2017/5/3.
 */
public abstract class TcpClientPackageBase {
    protected TcpClientBase clientBase = null;
    protected HeadType encodeHeadType = HeadType.HEAD_ID_ONLY;        // 编码消息头类型; 默认最简消息头, 只有msgId;
    protected HeadType decodeHeadType = HeadType.HEAD_ID_ONLY;        // 解码消息头类型; 默认最简消息头, 只有msgId;

    public void Init(TcpClientBase clientBase, HeadType encodeHeadType, HeadType decodeHeadType) {
        this.clientBase = clientBase;
        this.encodeHeadType = encodeHeadType;
        this.decodeHeadType = decodeHeadType;
    }

    /** 初始化, 端口绑定 */
    public abstract void Connect(String ip, int port);

    /** 主动断开连接 */
    public abstract void close();

    /** 主动发送消息 */
    public abstract boolean write(Message msg);
    public abstract boolean flush();
    public abstract boolean writeAndFlush(Message msg);
}
