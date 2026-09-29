package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.ScheduledNotificationDao;

public class ScheduledNotificationDTO {
    private long scheduleId;
    private String title;
    private String message;
    private String kind;
    private String audience;
    private long scheduledForMillis;
    private boolean released;
    private boolean cancelled;

    public ScheduledNotificationDTO() {
    }

    public ScheduledNotificationDTO(ScheduledNotificationDao dao) {
        this.scheduleId = dao.getScheduleId();
        this.title = dao.getTitle();
        this.message = dao.getMessage();
        this.kind = dao.getKind();
        this.audience = dao.getAudience();
        this.scheduledForMillis = dao.getScheduledFor() == null ? 0 : dao.getScheduledFor().getTime();
        this.released = dao.isReleased();
        this.cancelled = dao.isCancelled();
    }

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

    public long getScheduledForMillis() {
        return scheduledForMillis;
    }

    public void setScheduledForMillis(long scheduledForMillis) {
        this.scheduledForMillis = scheduledForMillis;
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
