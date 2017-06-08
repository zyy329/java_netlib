package com.zyyApp.netlib.server.Netty;

import com.zyyApp.LogMgr;
import com.zyyApp.netlib.protocol.Msg.Message;
import com.zyyApp.netlib.protocol.Netty.MsgDecoder;
import com.zyyApp.netlib.protocol.Netty.MsgEncoder;
import com.zyyApp.netlib.server.TcpServerPackageBase;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 基于 netty 网络库的, server 接口
 * Created by zyy on 2017/4/1.
 */
public class NettyServer extends TcpServerPackageBase{
    private ChannelMgr channelMgr = new ChannelMgr();

    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;
    private ChannelFuture channelFuture = null;

    /** 初始化, 端口绑定 */
    public void Init(int port, int workThreadNum) {
        bossGroup = new NioEventLoopGroup();     // Boss 线程组;
        if (workThreadNum == -1) {
            workerGroup = new NioEventLoopGroup();                  // 业务线程组;
        } else {
            workerGroup = new NioEventLoopGroup(workThreadNum);     // 业务线程组;
        }
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class); // (3)
            b.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new MsgEncoder(encodeHeadType)
                            , new MsgDecoder(decodeHeadType)
                            , new ServerJobHandler(serverBase, channelMgr));
                }
            });
            b.option(ChannelOption.SO_BACKLOG, 128);            // 标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度;
            b.childOption(ChannelOption.SO_KEEPALIVE, true);    // 心跳保活机制, 两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活;

            // Bind and start to accept incoming connections.
            channelFuture = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            //channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LogMgr.log.error("", e);
            System.exit(1);
        }/* finally {
            //shutdown();
        }*/
    }

    /** 关闭; 结束时需调用; */
    public void shutdown() {
        try {
            // shutdown EventLoopGroup
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
            channelFuture.channel().closeFuture().sync();   // close port
        } catch (InterruptedException e) {
            LogMgr.log.error("", e);
        }
    }

    /** 主动断开单个连接
     * @param connectId, 连接ID; null 表示断开所有连接;
     * */
    public void closeClient(Long connectId)
    {
        if (connectId == null) {
            // 断开所有连接;
            for (Channel channel : channelMgr.getAllChannel()) {
                if (channel != null) {
                    channel.close();
                }
            }
            // channelMgr.clearAll();       // channelInactive 中有进行处理;
        } else {
            // 断开单个连接;
            Channel channel = channelMgr.findChannel(connectId);
            if (channel != null) {
                channel.close();
            }
            // channelMgr.removeChannel(connectId);     // channelInactive 中有进行处理;
        }
    }

    /** 主动发送消息 */
    final public boolean write(Long connectId, Message msg) {
        Channel channel = channelMgr.findChannel(connectId);
        if (channel == null) {
            LogMgr.log.error("NettyServer::write() -- err; channel == null; [id:{}][{}]"
                    , connectId, msg.toString());
            return false;
        }
        if (channel.isWritable() == false) {
            LogMgr.log.error("NettyServer::write() -- isWritable == false; [id:{}][{}]"
                    , connectId, msg.toString());
            return false;
        }

        channel.write(msg);
        return true;
    }
    final public boolean flush(Long connectId) {
        Channel channel = channelMgr.findChannel(connectId);
        if (channel == null) {
            LogMgr.log.error("NettyServer::flush() -- err; channel == null; [id:{}]"
                    , connectId);
            return false;
        }

        channel.flush();
        return true;
    }
    final public boolean writeAndFlush(Long connectId, Message msg) {
        Channel channel = channelMgr.findChannel(connectId);
        if (channel == null) {
            LogMgr.log.error("NettyServer::writeAndFlush() -- err; channel == null; [id:{}][{}]"
                    , connectId, msg.toString());
            return false;
        }
        if (channel.isWritable() == false) {
            LogMgr.log.error("NettyServer::writeAndFlush() -- isWritable == false; [id:{}][{}]"
                    , connectId, msg.toString());
            return false;
        }

        channel.writeAndFlush(msg);
        return true;
    }
}
