package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.SalesmanOrdersDao;
import com.dairymart.dairyappserver.dao.SalesmanOrdersId;
import com.dairymart.dairyappserver.dao.SalesmanToRetailDao;
import com.dairymart.dairyappserver.dao.SalesmanToRetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesmanOrdersRepository extends JpaRepository<SalesmanOrdersDao, SalesmanOrdersId> {

}
