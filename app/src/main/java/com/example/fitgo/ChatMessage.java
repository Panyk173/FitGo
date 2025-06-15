package com.example.fitgo;

// ChatMessage.java
public class ChatMessage {
    public final String senderId, text, time;
    public ChatMessage(String senderId, String text, String time) {
        this.senderId = senderId;
        this.text     = text;
        this.time     = time;
    }
}

