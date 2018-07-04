package com.joshua.broker.netty;

import com.joshua.broker.serialize.MessageCodecUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageObjectEncoder extends MessageToByteEncoder<Object> {
    private MessageCodecUtil messageCodecUtil;

    public MessageObjectEncoder(final MessageCodecUtil messageCodecUtil) {
        this.messageCodecUtil = messageCodecUtil;
    }

    protected void encode(final ChannelHandlerContext ctx, final Object o, final ByteBuf out) throws Exception {
        messageCodecUtil.encode(out, o);
    }
}
