package com.dairymart.dairyappserver.dao;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "scheduled_notification", schema = "public")
public class ScheduledNotificationDao {

    @Id
    @Column(name = "scheduleid")
    @SequenceGenerator(name = "SCHEDULED_NOTIFICATION_SEQ", sequenceName = "scheduled_notification_seq", allocationSize = 1)
    @GeneratedValue(generator = "SCHEDULED_NOTIFICATION_SEQ", strategy = GenerationType.SEQUENCE)
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

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public Timestamp getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(Timestamp scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public boolean isReleased() {
        return released;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
