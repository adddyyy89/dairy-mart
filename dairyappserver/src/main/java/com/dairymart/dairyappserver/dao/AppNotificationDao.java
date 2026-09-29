package com.dairymart.dairyappserver.dao;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "app_notification", schema = "public")
public class AppNotificationDao {

    @Id
    @Column(name = "notificationid")
    @SequenceGenerator(name = "APP_NOTIFICATION_SEQ", sequenceName = "app_notification_seq", allocationSize = 1)
    @GeneratedValue(generator = "APP_NOTIFICATION_SEQ", strategy = GenerationType.SEQUENCE)
    private long notificationId;

    @Column(name = "userid")
    private int userId;

    @Column(name = "kind")
    private String kind;

    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @Column(name = "refid")
    private long refId;

    @Column(name = "isread")
    private boolean read;

    @Column(name = "createdon")
    private Timestamp createdOn;

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
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }
}
