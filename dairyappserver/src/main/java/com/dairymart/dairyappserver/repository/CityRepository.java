package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.BranchDao;
import com.dairymart.dairyappserver.dao.CityDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<CityDao, Integer> {
    
}
