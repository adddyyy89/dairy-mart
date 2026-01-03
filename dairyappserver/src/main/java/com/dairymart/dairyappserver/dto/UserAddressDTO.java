package com.dairymart.dairyappserver.dto;

import com.dairymart.dairyappserver.dao.UserAddressDao;

public class UserAddressDTO {

    private int addressId;

    private String fullAddress;

    private int cityId;

    private CityDTO city;

    public UserAddressDTO(){

    }

    public UserAddressDTO(UserAddressDao dao) {
        this.addressId = dao.getAddressId();
        this.cityId = dao.getCityId();
        this.fullAddress = dao.getFullAddress();
        if(dao.getCity() != null)
            this.city = new CityDTO(dao.getCity());
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

    public CityDTO getCity() {
        return city;
    }

    public void setCity(CityDTO city) {
        this.city = city;
    }
}
