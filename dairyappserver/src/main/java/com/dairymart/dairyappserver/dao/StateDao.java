package com.dairymart.dairyappserver.dao;

import jakarta.persistence.*;

@Entity
@Table(name = "State", schema = "public")
public class StateDao {

    @Id
    @Column(name = "stateid")
    private int stateId;

    @Column(name = "statename")
    private String stateName;

    @Column(name = "countryid")
    private int countryId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "countryid", insertable = false, updatable = false)
    private CountryDAO country;

    public StateDao() {

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

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public CountryDAO getCountry() {
        return country;
    }

    public void setCountry(CountryDAO country) {
        this.country = country;
    }
}
