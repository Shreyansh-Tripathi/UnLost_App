package com.example.unlost.notification;

public class Data {
    private String title, message;
    private int notificationId;

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data(String title, String message, int notificationId) {
        this.title = title;
        this.message = message;
        this.notificationId = notificationId;
    }
}
