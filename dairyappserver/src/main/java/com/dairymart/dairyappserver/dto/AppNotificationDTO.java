package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.AppNotificationDao;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class AppNotificationDTO {

    private long notificationId;
    private int userId;
    private String kind;
    private String title;
    private String message;
    private long refId;
    @JsonProperty("isRead")
    private boolean isRead;
    private Timestamp createdOn;
    private String audience;
    private long scheduledForMillis;

    public AppNotificationDTO() {
    }

    public AppNotificationDTO(AppNotificationDao dao) {
        this.notificationId = dao.getNotificationId();
        this.userId = dao.getUserId();
        this.kind = dao.getKind();
        this.title = dao.getTitle();
        this.message = dao.getMessage();
        this.refId = dao.getRefId();
        this.isRead = dao.isRead();
        this.createdOn = dao.getCreatedOn();
    }

    public long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
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

    public long getRefId() {
        return refId;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public long getScheduledForMillis() {
        return scheduledForMillis;
    }

    public void setScheduledForMillis(long scheduledForMillis) {
        this.scheduledForMillis = scheduledForMillis;
    }
}
