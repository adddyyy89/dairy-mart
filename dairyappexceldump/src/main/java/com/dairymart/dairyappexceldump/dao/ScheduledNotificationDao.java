package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "scheduled_notification", schema = "public")
public class ScheduledNotificationDao {

    @Id
    @Column(name = "scheduleid")
    private long scheduleId;

    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @Column(name = "kind")
    private String kind;

    @Column(name = "audience")
    private String audience;

    @Column(name = "scheduledfor")
    private Timestamp scheduledFor;

    @Column(name = "createdon")
    private Timestamp createdOn;

    @Column(name = "released")
    private boolean released;

    @Column(name = "cancelled")
    private boolean cancelled;

    public long getScheduleId() {
        return scheduleId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getKind() {
        return kind;
    }

    public String getAudience() {
        return audience;
    }

    public Timestamp getScheduledFor() {
        return scheduledFor;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public boolean isReleased() {
        return released;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
