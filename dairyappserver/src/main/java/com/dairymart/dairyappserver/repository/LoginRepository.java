package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.UserLoginDao;
import com.dairymart.dairyappserver.dao.UserLoginId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<UserLoginDao, UserLoginId> {
}
