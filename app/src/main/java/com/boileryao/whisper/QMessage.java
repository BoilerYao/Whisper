package com.boileryao.whisper;

/**
 * Created by BoilerYao on 2016/3/16.
 * QMessage, message class of communicate.
 */
class QMessage {
    private String content;
    private String sender;
    private long time;

    QMessage(String content, long time, String sender) {
        this.content = content;
        this.time = time;
        this.sender = sender;

    }

    String getContent() {
        return content;
    }

    String getSender() {
        return sender;
    }

    long getTime() {
        return time;
    }
}
