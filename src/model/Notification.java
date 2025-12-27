package model;

import java.time.LocalDateTime;

public class Notification {
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;
    private String type;

    public Notification(String content, String type) {
        this.content = content;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    //  Phải có hàm này để MainFrame lấy được chữ ra hiển thị
    public String getContent() {
        return content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}