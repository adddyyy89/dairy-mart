package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dao.UserTypeDao;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.dto.UserTypeDTO;
import com.dairymart.dairyappserver.repository.UserRepository;
import com.dairymart.dairyappserver.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserTypeService {

    @Autowired
    private UserTypeRepository userTypeRepository;

    public List<UserTypeDao> getAllUserTypes() {
        return userTypeRepository.findAll();
    }

    public UserTypeDao updateUserType(UserTypeDTO dto) {
        if(dto.getUserTypeId() <= 0) {
            return null;
        }

        Optional<UserTypeDao> dao = userTypeRepository.findById(dto.getUserTypeId());
        UserTypeDao userTypeDao = dao.orElse(null);

        if(userTypeDao != null) {
            userTypeDao.setActive(dto.getActive());
            userTypeDao.setUserTypeDesc(dto.getUserTypeDesc());
            return userTypeRepository.save(userTypeDao);
        }
        return null;
    }

    public UserTypeDao createUserType(UserTypeDao userTypeDao) {
        return userTypeRepository.save(userTypeDao);
    }

    public UserTypeDao findById(int id) {
        Optional<UserTypeDao> userTypeDao = userTypeRepository.findById(id);
        return userTypeDao.orElse(null);
    }
}
