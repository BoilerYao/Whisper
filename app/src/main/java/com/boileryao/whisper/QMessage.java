package com.boileryao.whisper;

import java.io.Serializable;

/**
 * Created by BoilerYao on 2016/3/16.
 */
class QMessage implements Serializable{
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    private String content;
    private int type;
    private long time;

    QMessage(String content, long time, int type) {
        this.content = content;
        this.time = time;
        this.type = type;

    }

    String getContent() {
        return content;
    }

    int getType() {
        return type;
    }

    long getTime() {
        return time;
    }
}
