package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.controller.CrateController;
import com.dairymart.dairyappserver.dao.BranchDao;
import com.dairymart.dairyappserver.dao.CrateDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.BranchDTO;
import com.dairymart.dairyappserver.dto.CrateDTO;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.repository.BranchRepository;
import com.dairymart.dairyappserver.repository.CrateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CrateService {

    Logger logger = LoggerFactory.getLogger(CrateService.class);

    @Autowired
    private CrateRepository crateRepository;

    @Autowired
    private UserService userService;

    public CrateDao updateCrate(CrateDao dao) {

        UserDao user = userService.findById(dao.getUserId());
        if(user == null) {
            logger.error("User id does not exist in system!!");
            return null;
        }

        if(user.getCrateCount() != dao.getCrateCount()) {
            logger.error("Crate count sent does not match with crate count with User!!");
        }

        if(dao.getRecordTimestamp() == null) {
            dao.setRecordTimestamp(new Timestamp(System.currentTimeMillis()));
        }
        if(dao.getUserTypeId() <= 0) {
            dao.setUserTypeId(userService.findByTypeId(dao.getUserId()).get(0).getTypeId());
        }

        int crateCount = dao.getCrateCount() + dao.getCrateReceived() - dao.getCrateReturned();
        user.setCrateCount(crateCount);
        UserDao updatedUser = userService.updateById(new UserDTO(user));
        if(updatedUser != null) {
            logger.info("User {} is updated with new crate count {}", updatedUser.getUserId(), updatedUser.getCrateCount());
        }
        else {
            logger.error("Unable to update User with the new crate count!!");
            return null;
        }

        return crateRepository.save(dao);
    }

    public List<CrateDao> getTotalCrates() {
        List<CrateDao> crateDaos = crateRepository.findAll();
        crateDaos.sort((o1, o2) -> o1.getRecordTimestamp().compareTo(o2.getRecordTimestamp()));
        return crateDaos;
    }

    public List<CrateDao> getTotalCratesForUser(int userId) {
        return crateRepository.findAll().stream().filter(c -> c.getUserId() == userId).collect(Collectors.toCollection(ArrayList::new));
    }

    public Integer getCurrentAssignedCrateForUser(int userId) {
        CrateDao dao = crateRepository.getCurrentAssignedCrateForUser(userId);
        int currentCrates = dao.getCrateCount() - dao.getCrateReturned() + dao.getCrateReceived();
        return currentCrates;
    }

}
