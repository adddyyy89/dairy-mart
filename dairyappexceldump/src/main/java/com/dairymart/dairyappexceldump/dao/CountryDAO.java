package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

@Entity
@Table(name = "Country", schema = "public")
public class CountryDAO {

    @Id
    @Column(name = "countryid")
    private int countryId;

    @Column(name = "countryname")
    private String countryName;

    public CountryDAO() {

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
