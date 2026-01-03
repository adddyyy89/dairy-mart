package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.CityDao;
import com.dairymart.dairyappserver.dao.StateDao;
import com.dairymart.dairyappserver.dao.UserAddressDao;
import com.dairymart.dairyappserver.repository.CityRepository;
import com.dairymart.dairyappserver.repository.UserAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAddressService {

    @Autowired
    private UserAddressRepository userAddressRepository;

    public UserAddressDao addNewAddress(UserAddressDao dao) {
        return userAddressRepository.save(dao);
    }
    
}
