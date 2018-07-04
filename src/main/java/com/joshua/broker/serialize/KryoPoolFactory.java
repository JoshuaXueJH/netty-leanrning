package com.joshua.broker.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.joshua.broker.model.RequestMessage;
import com.joshua.broker.model.ResponseMessage;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class KryoPoolFactory {
    private static KryoPoolFactory kryoPoolFactory = new KryoPoolFactory();

    private KryoPoolFactory() {
    }

    private KryoFactory factory = new KryoFactory() {
        public Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.register(RequestMessage.class);
            kryo.register(ResponseMessage.class);
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };

    private KryoPool kryoPool = new KryoPool.Builder(factory).build();

    public KryoPool getPool() {
        return kryoPool;
    }

    public static KryoPool getKryoPoolInstance() {
        return kryoPoolFactory.getPool();
    }
}
