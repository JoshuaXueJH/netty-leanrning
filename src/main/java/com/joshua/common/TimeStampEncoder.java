package com.joshua.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TimeStampEncoder extends MessageToByteEncoder<LoopBackTimeStamp> {

    protected void encode(ChannelHandlerContext channelHandlerContext, LoopBackTimeStamp loopBackTimeStamp, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(loopBackTimeStamp.toByteArray());
    }
}
