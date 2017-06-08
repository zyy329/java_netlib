package com.zyyApp.netlib.client;

import com.zyyApp.netlib.protocol.Msg.Head.HeadType;
import com.zyyApp.netlib.protocol.Msg.Message;

/**
 /**
 * 提供给应用层使用的 Tcp 客户端端接口
 * 作用是简化应用层的 网络使用; 隐藏背后的具体实现细节和调用框架;
 *
 */
public abstract class TcpClientBase {
    // 可以通过替换该对象实例, 达到替换底层网络框架, 而上层不受影响的目的;
    private TcpClientPackageBase clientPackage;

    public TcpClientBase(TcpClientPackageBase clientPackage, HeadType encodeHeadType, HeadType decodeHeadType) {
        this.clientPackage = clientPackage;
        this.clientPackage.Init(this, encodeHeadType, decodeHeadType);
    }

    /** 客户端申请向服务器建立连接诶;
     * */
    final public void Init(String ip, int port) {
        clientPackage.Connect(ip, port);
    }
    /** 主动关闭连接 */
    final public void close() {
        clientPackage.close();
    }

    /** 主动发送消息 */
    final public boolean write(Message msg) {
        return clientPackage.write(msg);
    }
    final public boolean flush() {
        return clientPackage.flush();
    }
    final public boolean writeAndFlush(Message msg) {
        return clientPackage.writeAndFlush(msg);
    }

    /* ******************************** 业务层需要重载的回调触发接口 ********************************* */
    /** 连接建立回调; 供子类回调使用*/
    public abstract void onConnect();

    /** 连接断开回调; 供子类回调使用*/
    public abstract void onDisConnect();

    /** 收到消息回调; */
    public abstract void onReceive(Message msg);
}
