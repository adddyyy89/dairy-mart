package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.SalesmanToRetailDao;
import com.dairymart.dairyappserver.dao.SalesmanToRetailId;
import com.dairymart.dairyappserver.dao.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesmanToRetailRepository extends JpaRepository<SalesmanToRetailDao, SalesmanToRetailId> {

}
