package com.chatroom_improved_version.model;

import java.time.LocalDateTime;

public class PrivateMessage {
    private final String sender;
    private final String receiver;
    private final String content;
    private final LocalDateTime time;
    private boolean read;

    public PrivateMessage(String sender, String receiver, String content, LocalDateTime time) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.time = time;
        this.read = false;
    }

    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getContent() { return content; }
    public LocalDateTime getTime() { return time; }
    public boolean isRead() { return read; }
    public void markAsRead() { this.read = true; }

    public boolean isBetween(String user1, String user2) {
        return (sender.equals(user1) && receiver.equals(user2)) ||
                (sender.equals(user2) && receiver.equals(user1));
    }
}