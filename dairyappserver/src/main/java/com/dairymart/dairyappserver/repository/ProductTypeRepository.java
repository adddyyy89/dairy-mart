package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.ProductDao;
import com.dairymart.dairyappserver.dao.ProductTypeDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductTypeDao, Integer> {

}
