package com.zyyApp.netlib.server;

import com.zyyApp.netlib.protocol.Msg.Head.HeadType;
import com.zyyApp.netlib.protocol.Msg.Message;

/**
 * 网络库封装基类
 * Created by zyy on 2017/5/3.
 */
public abstract class TcpServerPackageBase {
    protected TcpServerBase serverBase = null;
    protected HeadType encodeHeadType = HeadType.HEAD_ID_ONLY;        // 编码消息头类型; 默认最简消息头, 只有msgId;
    protected HeadType decodeHeadType = HeadType.HEAD_ID_ONLY;        // 解码消息头类型; 默认最简消息头, 只有msgId;

    public void Init(TcpServerBase serverBase, HeadType encodeHeadType, HeadType decodeHeadType) {
        this.serverBase = serverBase;
        this.encodeHeadType = encodeHeadType;
        this.decodeHeadType = decodeHeadType;
    }

    /** 初始化, 端口绑定 */
    public abstract void Init(int port, int workThreadNum);
    /** 关闭; 结束时需调用; */
    public abstract void shutdown();

    /** 主动断开单个连接
     * @param connectId, 连接ID; null 表示断开所有连接;
     * */
    public abstract void closeClient(Long connectId);


    /** 主动发送消息 */
    public abstract boolean write(Long connectId, Message msg);
    public abstract boolean flush(Long connectId);
    public abstract boolean writeAndFlush(Long connectId, Message msg);
}
