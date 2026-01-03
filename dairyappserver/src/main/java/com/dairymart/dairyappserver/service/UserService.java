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

        int userId = dto.getUserId();
        UserDao d = findById(userId);
        if(d == null) {
            return null;
        }

        UserDao dao = new UserDao(dto);
        dao.setLastUpdated(new Date(System.currentTimeMillis()));
        dao.setUserId(dto.getUserId());
        return userRepository.save(dao);


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
