package com.chatroom_improved_version.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.time.ZoneId;

public class Message {
    private final String sender;
    private final String content;
    private final LocalDateTime time;
    private final MessageType type;
    private final String targetUser;

    public enum MessageType {
        PUBLIC, PRIVATE, SYSTEM
    }

    // 公共消息构造函数
    public Message(String sender, String content, LocalDateTime time) {
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.type = MessageType.PUBLIC;
        this.targetUser = null;
    }

    // 私聊消息构造函数
    public Message(String sender, String targetUser, String content, LocalDateTime time) {
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.type = MessageType.PRIVATE;
        this.targetUser = targetUser;
    }

    // 系统消息构造函数
    public Message(String content, LocalDateTime time) {
        this.sender = "系统";
        this.content = content;
        this.time = time;
        this.type = MessageType.SYSTEM;
        this.targetUser = null;
    }

    // Getters
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public LocalDateTime getTime() { return time; }
    public MessageType getType() { return type; }
    public String getTargetUser() { return targetUser; }

    public boolean isPublic() { return type == MessageType.PUBLIC; }
    public boolean isPrivate() { return type == MessageType.PRIVATE; }
    public boolean isSystem() { return type == MessageType.SYSTEM; }

    // 新增：格式化时间的方法（最简单方案）
    public String getFormattedTime() {
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    // 新增：转换为Date的方法（如果需要）
    public Date getDate() {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }

    // 新增：返回时间戳
    public long getTimestamp() {
        return getDate().getTime();
    }
}