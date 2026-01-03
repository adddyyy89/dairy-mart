package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.SalesmanOrdersDao;
import com.dairymart.dairyappserver.dao.SalesmanToRetailDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dao.UserLoginDao;
import com.dairymart.dairyappserver.dto.SalesmanToRetailDTO;
import com.dairymart.dairyappserver.repository.LoginRepository;
import com.dairymart.dairyappserver.repository.SalesmanOrdersRepository;
import com.dairymart.dairyappserver.repository.SalesmanToRetailRepository;
import com.dairymart.dairyappserver.repository.UserRepository;
import com.dairymart.dairyappserver.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalesmanToRetailService {

    @Autowired
    private SalesmanToRetailRepository salesmanToRetailRepository;

    @Autowired
    private SalesmanOrdersRepository salesmanOrdersRepository;

    @Autowired
    private UserRepository userRepo;


    public List<SalesmanToRetailDao> getAllSalestoRetail() {
        return salesmanToRetailRepository.findAll();
    }

    public List<SalesmanToRetailDao> getAllRetailsforSalesman(int salesmanUserId) {
        return salesmanToRetailRepository.findAll().stream().filter(x -> x.getSalesmanId() == salesmanUserId).collect(Collectors.toCollection(ArrayList::new));
    }

    public SalesmanToRetailDao assignNewSalesToRetail(SalesmanToRetailDao salesmanToRetailDao) {
        return salesmanToRetailRepository.save(salesmanToRetailDao);
    }

    public List<SalesmanToRetailDao> getAllSalesmanToRetailForVehicle(String vehicleNumber) {
        return salesmanToRetailRepository.findAll().stream().filter(x -> x.getVehicleNumber().equalsIgnoreCase(vehicleNumber)).collect(Collectors.toCollection(ArrayList::new));

    }

    public List<SalesmanOrdersDao> getCurrentOrdersPlaced(int salesmanUserId) {
        List<SalesmanOrdersDao> salesmanOrdersDaos = salesmanOrdersRepository.findAll().stream().filter(x -> x.getSalesmanId() == salesmanUserId && DateUtil.isSameDay(x.getCreatedOn())).collect(Collectors.toCollection(ArrayList::new));
        return salesmanOrdersDaos;
    }

    public boolean removeSalesmanToRetailAssignment(int salesmanId, int retailerId){
        List<SalesmanToRetailDao> daoList = getAllRetailsforSalesman(salesmanId);
        SalesmanToRetailDao finalDao = null;
        int id = -1;
        for(SalesmanToRetailDao dao : daoList) {
            if(dao.getRetailerId() == retailerId) {
                finalDao = dao;
                break;
            }
        }
        if(finalDao != null) {
            finalDao.setActive(false);
            finalDao.setLastUpdated(new Date(System.currentTimeMillis()));
            salesmanToRetailRepository.save(finalDao);
            return true;
        }
        return false;
    }

    public UserDao getSalesmanForRetailer(int retailerId) {
        List<SalesmanToRetailDao> salesmanToRetailDaos = salesmanToRetailRepository.findAll().stream().filter(s -> s.getRetailerId() == retailerId).collect(Collectors.toCollection(ArrayList::new));
        return salesmanToRetailDaos.get(0).getSalesman();
    }



}
