package com.zyyApp.netlib.client.Netty;

import com.zyyApp.LogMgr;
import com.zyyApp.netlib.client.TcpClientBase;
import com.zyyApp.netlib.protocol.Msg.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * channel 事件处理;
 * Created by zyy on 2017/4/17.
 */
public class ClientJobHandler extends ChannelInboundHandlerAdapter {
    private TcpClientBase clientBase = null;
    private NettyClient nettyClient = null;

    public ClientJobHandler(TcpClientBase clientBase, NettyClient client) {
        this.clientBase = clientBase;
        this.nettyClient = client;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception { // (1)
        // 连接建立;
        nettyClient.setChannel(ctx.channel());
        clientBase.onConnect();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        // 连接断开;
        nettyClient.setChannel(null);
        clientBase.onDisConnect();
        super.channelInactive(ctx);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 收到消息;
        clientBase.onReceive((Message)msg);

        // 不再使用, 将消息对象还会重用池中;
        Message.poolPush((Message) msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        LogMgr.log.debug("client channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
