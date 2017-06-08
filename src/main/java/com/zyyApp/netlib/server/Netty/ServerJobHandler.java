package com.zyyApp.netlib.server.Netty;

import com.zyyApp.LogMgr;
import com.zyyApp.netlib.protocol.Msg.Message;
import com.zyyApp.netlib.server.TcpServerBase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * channel 事件处理;
 * Created by zyy on 2017/4/5.
 */
public class ServerJobHandler extends ChannelInboundHandlerAdapter {
    private TcpServerBase serverBase = null;
    private ChannelMgr channelMgr = null;

    public ServerJobHandler(TcpServerBase serverBase, ChannelMgr channelMgr) {
        this.serverBase = serverBase;
        this.channelMgr = channelMgr;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception { // (1)
        // 连接建立;
        Long connectId = channelMgr.addChannel(ctx.channel());
        serverBase.onConnect(connectId);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        // 连接断开;
        Long connectId = channelMgr.getUID(ctx.channel().id());
        channelMgr.removeChannel(connectId);
        serverBase.onDisConnect(connectId);
        super.channelInactive(ctx);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 收到消息;
        Long connectId = channelMgr.getUID(ctx.channel().id());
        serverBase.onReceive(connectId, (Message) msg);

        // 不再使用, 将消息对象还会重用池中;
        Message.poolPush((Message) msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        LogMgr.log.debug("server channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
