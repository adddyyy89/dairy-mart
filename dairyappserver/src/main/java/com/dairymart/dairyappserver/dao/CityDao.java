package com.dairymart.dairyappserver.dao;

import jakarta.persistence.*;

@Entity
@Table(name = "City", schema = "public")
public class CityDao {

    @Id
    @Column(name = "cityid")
    private int cityId;

    @Column(name = "cityname")
    private String cityName;

    @Column(name = "stateid")
    private int stateId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "stateid", insertable = false, updatable = false)
    private StateDao state;

    public CityDao() {

    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public StateDao getState() {
        return state;
    }

    public void setState(StateDao state) {
        this.state = state;
    }
}
