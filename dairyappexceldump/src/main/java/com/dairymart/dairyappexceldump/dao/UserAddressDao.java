package com.dairymart.dairyappexceldump.dao;

import jakarta.persistence.*;

@Entity
@Table(name = "UserAddress", schema = "public")
public class UserAddressDao {

    @Id
    @Column(name = "addressid")
    @SequenceGenerator(name = "USER_ADDRESS_SEQ", sequenceName = "user_address_seq", allocationSize = 1)
    @GeneratedValue(generator = "USER_ADDRESS_SEQ", strategy = GenerationType.SEQUENCE)
    private int addressId;

    @Column(name = "fulladdress")
    private String fullAddress;

    @Column(name = "cityid")
    private int cityId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "cityid", updatable = false, insertable = false)
    private CityDao city;

    public UserAddressDao(String fullAddress, int cityId) {
        this.fullAddress = fullAddress;
        this.cityId = cityId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public CityDao getCity() {
        return city;
    }

    public void setCity(CityDao city) {
        this.city = city;
    }

    public UserAddressDao() {
    }
}
