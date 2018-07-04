package com.joshua.server;

import com.joshua.common.TimeStampDecoder;
import com.joshua.common.TimeStampEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boosGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);

        final EventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(1500);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline channelPipeline = socketChannel.pipeline();
                channelPipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, 5));
                channelPipeline.addLast(new TimeStampEncoder());
                channelPipeline.addLast(new TimeStampDecoder());

                channelPipeline.addLast(eventExecutorGroup, "serverHandler", new ServerHandler());
            }
        });
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.bind(19000).sync();
    }
}
