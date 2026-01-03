package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.TrackingDao;
import com.dairymart.dairyappserver.dao.UserDao;
import jakarta.persistence.*;

import java.sql.Timestamp;

public class TrackingDTO {

    private int trackId;

    private int userId;

    private Double latitude;

    private Double longitude;

    private Boolean isActive;

    private String timestamp;

    private UserDTO user;

    public TrackingDTO() {
    }

    public TrackingDTO(int trackId, int userId, Double latitude, Double longitude, Boolean isActive, String timestamp) {
        this.trackId = trackId;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isActive = isActive;
        this.timestamp = timestamp;
    }

    public TrackingDTO(TrackingDao dao) {
        this.trackId = dao.getTrackId();
        this.isActive = dao.getActive();
        if(dao.getUser() != null) {
            this.user = new UserDTO(dao.getUser());
        }
        this.userId = dao.getUserId();
        this.latitude = dao.getLatitude();
        this.longitude = dao.getLongitude();
        this.timestamp = dao.getTimestamp().toString();
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
