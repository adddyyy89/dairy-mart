package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.ProductDao;
import com.dairymart.dairyappserver.dao.RetailOrderDao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RetailOrderRepository extends JpaRepository<RetailOrderDao, Integer> {

    List<RetailOrderDao> findByRetailerId(Integer retailerId);
}
