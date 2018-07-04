package com.joshua.broker.model;

public enum MessageSource {
    Consumer(1),
    Broker(2),
    Producer(3);

    private int source;

    MessageSource(int source) {
        this.source = source;
    }

    public int getSource() {
        return source;
    }
}
