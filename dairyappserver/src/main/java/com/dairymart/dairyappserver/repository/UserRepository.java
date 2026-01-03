package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserDao, Integer> {

}
