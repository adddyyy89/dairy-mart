package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.OrderStatusDao;
import com.dairymart.dairyappserver.dao.ProductDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatusDao, Integer> {

}
