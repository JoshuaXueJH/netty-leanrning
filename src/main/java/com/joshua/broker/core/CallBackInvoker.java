package com.joshua.broker.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CallBackInvoker<T> {
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private T messageResult;
    private List<CallBackListener<T>> listeners = Collections.synchronizedList(new ArrayList<CallBackListener<T>>());
    private String requestId;
    private Throwable reason;

    public CallBackInvoker() {
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setMessageResult(T messageResult) {
        this.messageResult = messageResult;
        publish();
        countDownLatch.countDown();
    }

    public Object getMessageResult(long timeout, TimeUnit timeUnit) {
        try {
            countDownLatch.await(timeout, timeUnit);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        if (reason != null) {
            return null;
        }
        return messageResult;
    }


    public void setReason(Throwable reason) {
        this.reason = reason;
        publish();
        countDownLatch.countDown();
    }

    public void join(CallBackListener<T> listener) {
        this.listeners.add(listener);
    }

    private void publish() {
        for (CallBackListener<T> listener : listeners) {
            listener.onCallBack(messageResult);
        }
    }
}
