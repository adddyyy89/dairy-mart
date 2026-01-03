package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.BranchDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.BranchDTO;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.repository.BranchRepository;
import com.dairymart.dairyappserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    public List<BranchDao> getAllBranches() {
        return branchRepository.findAll();
    }

    public BranchDao createBranch(BranchDao branch) {
        return branchRepository.save(branch);
    }

    public BranchDao findById(int id) {
        Optional<BranchDao> branchDao = branchRepository.findById(id);
        return branchDao.orElse(null);
    }

    public BranchDao updateById(BranchDTO dto) {

        int branchId = dto.getBranchId();
        BranchDao d = findById(branchId);
        if(d == null) {
            return null;
        }

        BranchDao dao = new BranchDao(dto);
        dao.setBranchId(dto.getBranchId());
        dao.setLastUpdated(new Date(System.currentTimeMillis()));
        return branchRepository.save(dao);

    }


}
