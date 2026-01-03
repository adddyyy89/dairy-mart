package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.UserAddressDao;
import com.dairymart.dairyappserver.dao.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddressDao, Integer> {

}
