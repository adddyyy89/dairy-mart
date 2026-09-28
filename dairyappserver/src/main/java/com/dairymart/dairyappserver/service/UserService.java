package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.UserAddressDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAddressService userAddressService;


    public List<UserDao> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDao createUser(UserDao user) {
        user.setPassword(storedPassword(user.getPassword()));
        UserAddressDao addressDao = userAddressService.addNewAddress(user.getAddress());
        if(addressDao != null) {
            user.setAddressId(addressDao.getAddressId());
            return userRepository.save(user);
        }
        return null;
    }

    public boolean validateAdminUser(UserDTO user) {
        return user.getUserTypeId() > 0 && !user.getFirstName().isEmpty() && !user.getEmailId().isEmpty() && !user.getPhoneNumber().isEmpty() && !user.getPassword().isEmpty();
    }

    public UserDao findById(int id) {
        Optional<UserDao> userDao = userRepository.findById(id);
        return userDao.orElse(null);
    }

    public int findRoleByPhone(String phoneNumber) {
        String pNumber = phoneNumber;
        List<UserDao> dao = userRepository.findAll().stream().filter(x -> x.getPhoneNumber().equalsIgnoreCase(pNumber)).collect(Collectors.toCollection(ArrayList::new));
        return dao.get(0).getTypeId();
    }

    public UserDao findByPhone(String phoneNumber) {
        String pNumber = phoneNumber;
        List<UserDao> dao = userRepository.findAll().stream().filter(x -> x.getPhoneNumber().equalsIgnoreCase(pNumber)).collect(Collectors.toCollection(ArrayList::new));
        return dao.get(0);
    }

    public List<UserDao> findByTypeId(int typeId) {
        List<UserDao> dao = userRepository.findAll().stream().filter(x -> x.getTypeId() == typeId).collect(Collectors.toCollection(ArrayList::new));
        return dao;
    }

    public UserDao updateById(UserDTO dto) {
        UserDao d = findById(dto.getUserId());
        if (d == null) {
            return null;
        }

        if (dto.getFirstName() != null && !dto.getFirstName().isEmpty()) {
            d.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            d.setLastName(dto.getLastName());
        }
        if (dto.getEmailId() != null && !dto.getEmailId().isEmpty()) {
            d.setEmailId(dto.getEmailId());
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty()) {
            d.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            d.setPassword(storedPassword(dto.getPassword()));
        }
        if (dto.getUserTypeId() > 0) {
            d.setTypeId(dto.getUserTypeId());
        }
        if (dto.getAddressId() > 0) {
            d.setAddressId(dto.getAddressId());
        }
        if (dto.getActive() != null) {
            d.setActive(dto.getActive());
        }
        d.setLastUpdated(new Date(System.currentTimeMillis()));
        return userRepository.save(d);
    }

    public static String storedPassword(String password) {
        if (password == null || password.isEmpty()) {
            return password;
        }
        if (password.startsWith("{")) {
            return password;
        }
        return "{noop}" + password;
    }

    public UserDao saveUser(UserDao dao) {
        return userRepository.save(dao);
    }

    public List<UserDao> getRetailers(List<UserDao> userDaos) {
        return userDaos.stream().filter(dao -> dao.getTypeId() == 3).toList();
    }

    public List<UserDao> getSalesman(List<UserDao> userDaos) {
        return userDaos.stream().filter(dao -> dao.getTypeId() == 2).toList();
    }


}
