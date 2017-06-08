package com.zyyApp.netlib.server;

import com.zyyApp.netlib.protocol.Msg.Head.HeadType;
import com.zyyApp.netlib.protocol.Msg.Message;

/**
 *  提供给应用层使用的 Tcp 服务端接口
 *  作用是简化应用层的 网络使用; 隐藏背后的具体实现细节和调用框架;
 */
public abstract class TcpServerBase
{
    // 可以通过替换该对象实例, 达到替换底层网络框架, 而上层不受影响的目的;
    private TcpServerPackageBase serverPackage;

    public TcpServerBase(TcpServerPackageBase serverPackage, HeadType encodeHeadType, HeadType decodeHeadType) {
        this.serverPackage = serverPackage;
        this.serverPackage.Init(this, encodeHeadType, decodeHeadType);
    }

    /** 初始化服务器, 端口绑定
     * @param workThreadNum, 业务线程的线程数; -1 表示使用默认值;
     * */
    final public void Init(int port, int workThreadNum) {
        serverPackage.Init(port, workThreadNum);
    }
    /** 关闭服务器, 释放资源; */
    final public void shutdown() {
        serverPackage.shutdown();
    }
    /** 主动断开单个连接
     * @param connectId, 连接ID; null 表示断开所有连接;
     * */
    final public void closeClient(Long connectId) {
        serverPackage.closeClient(connectId);
    }

    /** 主动发送消息 */
    final public boolean write(Long connectId, Message msg) {
        return serverPackage.write(connectId, msg);
    }
    final public boolean flush(Long connectId) {
        return serverPackage.flush(connectId);
    }
    final public boolean writeAndFlush(Long connectId, Message msg) {
        return serverPackage.writeAndFlush(connectId, msg);
    }

    /* ******************************** 业务层需要重载的回调触发接口 ********************************* */
    /** 新连接建立回调; 供子类回调使用*/
    public abstract void onConnect(Long connectId);

    /** 连接断开回调; 供子类回调使用*/
    public abstract void onDisConnect(Long connectId);

    /** 收到消息回调; */
    public abstract void onReceive(Long connectId, Message msg);
}