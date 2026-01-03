package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.RetailOrderDao;
import com.dairymart.dairyappserver.dao.RetailOrderDetailsDao;
import com.dairymart.dairyappserver.dao.RetailOrderDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetailOrderDetailsRepository extends JpaRepository<RetailOrderDetailsDao, RetailOrderDetailsId> {

}
