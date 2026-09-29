package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.CratePoolDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CratePoolRepository extends JpaRepository<CratePoolDao, Integer> {
}
