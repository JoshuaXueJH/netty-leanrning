package com.joshua.broker.model;

import com.joshua.broker.msg.BaseMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessMessage {
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;

    protected String msgId;
    protected BaseMessage msgParams;
    protected MessageSource msgSource;
    protected MessageType msgType;
}
