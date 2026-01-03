package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.CityDao;
import com.dairymart.dairyappserver.dao.StateDao;
import com.dairymart.dairyappserver.repository.CityRepository;
import com.dairymart.dairyappserver.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private StateService stateService;

    public List<CityDao> getAllCities() {
        return cityRepository.findAll();
    }

    public List<CityDao> getCitiesForState(int stateId) {
        StateDao stateDao = stateService.getStateById(stateId);
        if(stateDao == null) {
            return null;
        }

        return cityRepository.findAll().stream().filter(city -> city.getStateId() == stateId).collect(Collectors.toCollection(ArrayList::new));
    }
    
}
