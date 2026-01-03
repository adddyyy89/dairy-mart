package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.CityDao;

public class CityDTO {

    private int cityId;

    private String cityName;

    private int stateId;

    private StateDTO state;

    public CityDTO() {

    }

    public CityDTO(CityDao dao) {
        this.cityId = dao.getCityId();
        this.cityName = dao.getCityName();
        this.stateId = dao.getStateId();
        if(dao.getState() != null)
            this.state = new StateDTO(dao.getState());
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

    public StateDTO getState() {
        return state;
    }

    public void setState(StateDTO state) {
        this.state = state;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }
}
