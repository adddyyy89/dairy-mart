package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.BranchInventoryDao;
import com.dairymart.dairyappserver.dao.BranchInventoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchInventoryRepository extends JpaRepository<BranchInventoryDao, BranchInventoryId> {
}
