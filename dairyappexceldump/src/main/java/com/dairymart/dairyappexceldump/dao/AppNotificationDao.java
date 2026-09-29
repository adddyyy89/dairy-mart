package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "app_notification", schema = "public")
public class AppNotificationDao {

    @Id
    @Column(name = "notificationid")
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

    public int getUserId() {
        return userId;
    }

    public String getKind() {
        return kind;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getRefId() {
        return refId;
    }

    public boolean isRead() {
        return read;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }
}
