package com.qlcvht.websocket;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage implements Serializable {
    private String fromId;       // MaSV hoac MaCVHT
    private String fromName;     // Ten nguoi gui
    private String fromRole;     // SINH_VIEN hoac CO_VAN
    private String toId;         // Nguoi nhan
    private String title;        // Tieu de
    private String content;      // Noi dung tin nhan
    private String timestamp;    // Thoi gian gui
    private String type;         // CHAT, NOTIFICATION, SYSTEM

    public ChatMessage() {
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.type = "CHAT";
    }

    public ChatMessage(String fromId, String fromName, String fromRole, String toId, String title, String content) {
        this();
        this.fromId = fromId;
        this.fromName = fromName;
        this.fromRole = fromRole;
        this.toId = toId;
        this.title = title;
        this.content = content;
    }

    // Convert to custom format or JSON-like String
    public String serialize() {
        // format: TYPE||FROM_ID||FROM_NAME||FROM_ROLE||TO_ID||TITLE||CONTENT||TIMESTAMP
        return (type != null ? type : "CHAT") + "||"
             + escape(fromId) + "||"
             + escape(fromName) + "||"
             + escape(fromRole) + "||"
             + escape(toId) + "||"
             + escape(title) + "||"
             + escape(content) + "||"
             + escape(timestamp);
    }

    public static ChatMessage deserialize(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        String[] parts = raw.split("\\|\\|", -1);
        if (parts.length < 8) return null;

        ChatMessage msg = new ChatMessage();
        msg.setType(parts[0]);
        msg.setFromId(unescape(parts[1]));
        msg.setFromName(unescape(parts[2]));
        msg.setFromRole(unescape(parts[3]));
        msg.setToId(unescape(parts[4]));
        msg.setTitle(unescape(parts[5]));
        msg.setContent(unescape(parts[6]));
        msg.setTimestamp(unescape(parts[7]));
        return msg;
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n").replace("\\r", "\r");
    }

    public String getFromId() { return fromId; }
    public void setFromId(String fromId) { this.fromId = fromId; }

    public String getFromName() { return fromName; }
    public void setFromName(String fromName) { this.fromName = fromName; }

    public String getFromRole() { return fromRole; }
    public void setFromRole(String fromRole) { this.fromRole = fromRole; }

    public String getToId() { return toId; }
    public void setToId(String toId) { this.toId = toId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
