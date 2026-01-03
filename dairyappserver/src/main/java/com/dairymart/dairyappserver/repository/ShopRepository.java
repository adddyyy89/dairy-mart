package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.ProductDao;
import com.dairymart.dairyappserver.dao.ShopDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<ShopDao, Integer> {

}
