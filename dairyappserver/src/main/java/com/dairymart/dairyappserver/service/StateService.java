package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.BranchDao;
import com.dairymart.dairyappserver.dao.StateDao;
import com.dairymart.dairyappserver.dto.BranchDTO;
import com.dairymart.dairyappserver.repository.BranchRepository;
import com.dairymart.dairyappserver.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StateService {

    @Autowired
    private StateRepository stateRepository;

    public List<StateDao> getAllStates() {
        return stateRepository.findAll();
    }

    public StateDao getStateById(int stateId) {
        Optional<StateDao> dao = stateRepository.findById(stateId);
        return dao.orElse(null);
    }

}
