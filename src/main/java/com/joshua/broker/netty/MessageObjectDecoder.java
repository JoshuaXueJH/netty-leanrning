package com.joshua.broker.netty;

import com.joshua.broker.serialize.MessageCodecUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageObjectDecoder extends ByteToMessageDecoder {
    public static final int MESSAGE_LENGTH = MessageCodecUtil.MESSAGE_LENGTH;

    private MessageCodecUtil messageCodecUtil;

    public MessageObjectDecoder(final MessageCodecUtil messageCodecUtil) {
        this.messageCodecUtil = messageCodecUtil;
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < MESSAGE_LENGTH) {
            return;
        }

        in.markReaderIndex();
        int messageLength = in.readInt();
        if (messageLength < 0) {
            ctx.close();
        }

        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        } else {
            byte[] messageBody = new byte[messageLength];
            in.readBytes(messageBody);

            try {
                Object result = messageCodecUtil.decode(messageBody);
                out.add(result);
            } catch (IOException e) {
                Logger.getLogger(MessageObjectDecoder.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
}
