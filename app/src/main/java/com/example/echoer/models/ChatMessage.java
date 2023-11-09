package com.example.echoer.models;

public class ChatMessage {
    private final String dateTime;
    private final String message;
    private final String senderName;
    private final int isSent;    // 处理该信息展示在左边还是右边

    public ChatMessage(String dateTime, String message, String senderName, int isSent) {
        this.dateTime = dateTime;
        this.message = message;
        this.senderName = senderName;
        this.isSent = isSent;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderName() {
        return senderName;
    }

    public int getIsSent() {
        return isSent;
    }
}
