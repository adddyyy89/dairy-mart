package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "tracking", schema = "public")
public class TrackingDao {

    @Id
    @Column(name = "trackid")
    @SequenceGenerator(name = "TRACK_ID", sequenceName = "tracking_seq", allocationSize = 1)
    @GeneratedValue(generator = "TRACK_ID", strategy = GenerationType.SEQUENCE)
    private int trackId;

    @Column(name = "userid")
    private int userId;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "isactive")
    private Boolean isActive;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", insertable = false, updatable = false)
    private UserDao user;

    public TrackingDao() {
    }

    public TrackingDao(int trackId, int userId, Double latitude, Double longitude, Boolean isActive, Timestamp timestamp, UserDao type) {
        this.trackId = trackId;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isActive = isActive;
        this.timestamp = timestamp;
        if(type != null)
            this.user = type;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public UserDao getUser() {
        return user;
    }

    public void setUser(UserDao user) {
        this.user = user;
    }
}
