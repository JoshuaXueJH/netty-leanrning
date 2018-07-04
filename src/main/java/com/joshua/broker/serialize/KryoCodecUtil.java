package com.joshua.broker.serialize;

import com.esotericsoftware.kryo.pool.KryoPool;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoCodecUtil implements MessageCodecUtil {
    private KryoPool kryoPool;

    public KryoCodecUtil(KryoPool kryoPool) {
        this.kryoPool = kryoPool;
    }

    public void encode(final ByteBuf out, final Object message) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            KryoSerialize kryoSerialize = new KryoSerialize(kryoPool);
            kryoSerialize.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
        } finally {
            byteArrayOutputStream.close();
        }
    }

    public Object decode(byte[] body) throws IOException {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(body);
            KryoSerialize kryoSerialize = new KryoSerialize(kryoPool);
            Object result = kryoSerialize.deserialize(byteArrayInputStream);
            return result;
        } finally {
            byteArrayInputStream.close();
        }
    }
}
