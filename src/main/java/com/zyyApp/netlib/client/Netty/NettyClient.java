package com.zyyApp.netlib.client.Netty;

import com.zyyApp.LogMgr;
import com.zyyApp.netlib.client.TcpClientPackageBase;
import com.zyyApp.netlib.protocol.Msg.Message;
import com.zyyApp.netlib.protocol.Netty.MsgDecoder;
import com.zyyApp.netlib.protocol.Netty.MsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * 基于 netty 网络库的, Client 接口
 * Created by zyy on 2017/4/13.
 */
public class NettyClient extends TcpClientPackageBase{
    //private ChannelFuture channelFuture = null;
    private Channel channel = null;

    /** 初始化, 端口绑定 */
    public void Connect(String ip, int port) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();                  // 业务线程组;

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new MsgEncoder(encodeHeadType)
                            , new MsgDecoder(decodeHeadType)
                            , new ClientJobHandler(clientBase, NettyClient.this));
                }
            });

            // Start the client.
            /*channelFuture = */b.connect(ip, port).sync(); // (5)

            // Wait until the connection is closed.
            //channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LogMgr.log.error("", e);
            System.exit(1);
        }/* finally {
            //shutdown();
        }*/
    }

    /** 主动断开连接 */
    public void close() {
        if (channel != null) {
            channel.close();
        }
        channel = null;
    }

    /** 主动发送消息 */
    final public boolean write(Message msg) {
        if (channel == null) {
            LogMgr.log.error("NettyClient::write() -- err; channel == null; [{}]", msg.toString());
            return false;
        }
        if (channel.isWritable() == false) {
            LogMgr.log.error("NettyClient::write() -- isWritable == false; [{}]", msg.toString());
            return false;
        }

        channel.write(msg);
        return true;
    }
    final public boolean flush() {
        if (channel == null) {
            LogMgr.log.error("NettyClient::flush() -- err; channel == null;");
            return false;
        }

        channel.flush();
        return true;
    }
    final public boolean writeAndFlush(Message msg) {
        if (channel == null) {
            LogMgr.log.error("NettyClient::writeAndFlush() -- err; channel == null; [{}]", msg.toString());
            return false;
        }
        if (channel.isWritable() == false) {
            LogMgr.log.error("NettyClient::writeAndFlush() -- isWritable == false; [{}]", msg.toString());
            return false;
        }

        channel.writeAndFlush(msg);
        return true;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
