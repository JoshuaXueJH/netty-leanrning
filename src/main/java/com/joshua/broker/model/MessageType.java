package com.joshua.broker.model;

public enum MessageType {
    MQSubscribe(1),
    MQUnsbscribe(2),
    MQMessage(3),
    MQProducerACK(4),
    MQConsumerACK(5);

    private int messageType;

    MessageType(int messageType) {
        this.messageType = messageType;
    }

    int getMessageType() {
        return messageType;
    }
}
