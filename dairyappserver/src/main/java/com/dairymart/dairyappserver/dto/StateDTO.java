package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.CountryDAO;
import com.dairymart.dairyappserver.dao.StateDao;

public class StateDTO {

    private int stateId;

    private String stateName;

    private int countryId;

    private CountryDTO country;

    public StateDTO() {

    }

    public StateDTO(StateDao dao) {
        this.stateId = dao.getStateId();
        this.stateName = dao.getStateName();
        this.countryId = dao.getCountryId();
        if(dao.getCountry() != null)
            this.country = new CountryDTO(dao.getCountry());

    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }
}
