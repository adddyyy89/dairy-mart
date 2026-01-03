package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.BranchDao;
import com.dairymart.dairyappserver.dao.CrateDao;
import com.dairymart.dairyappserver.dao.CrateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CrateRepository extends JpaRepository<CrateDao, CrateId> {

    @Query(value = "select c from CrateDao c where c.userId = ?1 order by c.recordTimestamp DESC LIMIT 1")
    CrateDao getCurrentAssignedCrateForUser(int userId);
}
