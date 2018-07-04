package com.joshua.common;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Getter
@Setter
public class LoopBackTimeStamp {
    private long sendTimeStamp;
    private long receiveTimeStamp;

    public LoopBackTimeStamp() {
        this.sendTimeStamp = System.nanoTime();
    }

    public long timeLapseInNanoSecond() {
        return receiveTimeStamp - sendTimeStamp;
    }

    public byte[] toByteArray() {
        final int byteOfLong = Long.SIZE / Byte.SIZE;
        byte[] ba = new byte[byteOfLong * 2];
        byte[] t1 = ByteBuffer.allocate(byteOfLong).putLong(sendTimeStamp).array();
        byte[] t2 = ByteBuffer.allocate(byteOfLong).putLong(receiveTimeStamp).array();

        for (int i = 0; i < byteOfLong; i++) {
            ba[i] = t1[i];
        }

        for (int i = 0; i < byteOfLong; i++) {
            ba[byteOfLong + i] = t2[i];
        }

        return ba;
    }

    public void fromByteArray(byte[] content) {
        int len = content.length;
        final int byteOfLong = Long.SIZE / Byte.SIZE;
        if (len != byteOfLong * 2) {
            System.out.println("Error on content length. ");
            return;
        }
        ByteBuffer buf1 = ByteBuffer.allocate(byteOfLong).put(content, 0, byteOfLong);
        ByteBuffer buf2 = ByteBuffer.allocate(byteOfLong).put(content, byteOfLong, byteOfLong);
        buf1.rewind();
        buf2.rewind();
        this.sendTimeStamp = buf1.getLong();
        this.receiveTimeStamp = buf2.getLong();
    }
}