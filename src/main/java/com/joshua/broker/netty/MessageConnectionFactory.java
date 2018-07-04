package com.joshua.broker.netty;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.joshua.broker.core.CallBackInvoker;
import com.joshua.broker.serialize.KryoCodecUtil;
import com.joshua.broker.serialize.KryoPoolFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

public class MessageConnectionFactory {
    private SocketAddress remoteAddr = null;
    private ChannelInboundHandlerAdapter messageHandler = null;
    private Map<String, CallBackInvoker<Object>> callBackInvokerMap = new ConcurrentHashMap<String, CallBackInvoker<Object>>();
    private Bootstrap bootstrap = null;
    private long timeout = 10 * 1000;
    private boolean connected = false;
    private EventLoopGroup eventLoopGroup = null;
    private static KryoCodecUtil kryoCodecUtil = new KryoCodecUtil(KryoPoolFactory.getKryoPoolInstance());
    private Channel messageChannel = null;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private NettyClustersConfig nettyClustersConfig = new NettyClustersConfig();
    private ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("MessageConnectFactory-&d").setDaemon(true).build();

    public MessageConnectionFactory(String serverAddress) {
        String[] ipAddr = serverAddress.split("@");
        if (ipAddr.length == 2) {
            remoteAddr = NettyUtil.string2SocketAddress(serverAddress);
        }
    }

    public void setMessageHandler(ChannelInboundHandlerAdapter messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void init() {
        defaultEventExecutorGroup = new DefaultEventExecutorGroup(NettyClustersConfig.getWorkerThreads(), threadFactory);
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(defaultEventExecutorGroup);
                socketChannel.pipeline().addLast(new MessageObjectDecoder(kryoCodecUtil));
                socketChannel.pipeline().addLast(new MessageObjectEncoder(kryoCodecUtil));
                socketChannel.pipeline().addLast(messageHandler);
            }
        })
                .option(ChannelOption.SO_SNDBUF, nettyClustersConfig.getClientSocketSndBufSize())
                .option(ChannelOption.SO_RCVBUF, nettyClustersConfig.getClientSocketRcvBufSize())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false);
    }

    public void connect() {
        Preconditions.checkNotNull(messageHandler, "Message's handler is null. ");

        init();
        try {
            ChannelFuture channelFuture = bootstrap.connect(remoteAddr).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    Channel channel = channelFuture.channel();
                    messageChannel = channel;
                }
            });

            System.out.println("ip address: " + remoteAddr.toString());
            connected = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (messageChannel != null) {
            try {
                messageChannel.close().sync();
                eventLoopGroup.shutdownGracefully();
                defaultEventExecutorGroup.shutdownGracefully();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean traceInvoker(String key) {
        if (key == null) {
            return false;
        }
        return getCallBackInvokerMap().containsKey(key);
    }

    public CallBackInvoker<Object> detachInvoker(String key) {
        if (traceInvoker(key)) {
            return callBackInvokerMap.remove(key);
        } else {
            return null;
        }
    }

    public Map<String, CallBackInvoker<Object>> getCallBackInvokerMap() {
        return callBackInvokerMap;
    }

    public void setCallBackInvokerMap(Map<String, CallBackInvoker<Object>> callBackInvokerMap) {
        this.callBackInvokerMap = callBackInvokerMap;
    }
}
