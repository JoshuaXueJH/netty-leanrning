package com.joshua.broker.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.google.common.io.Closer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class KryoSerialize {
    private KryoPool kryoPool;
    private Closer closer = Closer.create();

    public KryoSerialize(final KryoPool kryoPool) {
        this.kryoPool = kryoPool;
    }

    public void serialize(OutputStream outputStream, Object object) throws IOException {
        try {
            Kryo kryo = kryoPool.borrow();
            Output output = new Output(outputStream);
            closer.register(outputStream);
            closer.register(output);
            kryo.writeClassAndObject(output, object);
            kryoPool.release(kryo);
        } finally {
            closer.close();
        }
    }

    public Object deserialize(InputStream inputStream) throws IOException {
        try {
            Kryo kryo = kryoPool.borrow();
            Input input = new Input(inputStream);
            closer.register(inputStream);
            closer.register(input);
            Object result = kryo.readClassAndObject(input);
            kryoPool.release(kryo);
            return result;
        } finally {
            closer.close();
        }
    }
}
