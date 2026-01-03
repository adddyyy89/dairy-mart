package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.CountryDAO;

public class CountryDTO {

    private int countryId;

    private String countryName;

    public CountryDTO(CountryDAO dao) {
        this.countryId = dao.getCountryId();
        this.countryName = dao.getCountryName();
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
