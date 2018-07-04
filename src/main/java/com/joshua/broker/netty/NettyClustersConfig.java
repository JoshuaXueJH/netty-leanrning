package com.joshua.broker.netty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NettyClustersConfig {
    private int clientSocketSndBufSize = 65535;
    private int clientSocketRcvBufSize = 65535;
    private static int workerThreads = Runtime.getRuntime().availableProcessors() * 2;

    public static int getWorkerThreads() {
        return workerThreads;
    }
}
