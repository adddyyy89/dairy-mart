package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.BranchDao;
import com.dairymart.dairyappserver.dao.TrackingDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingRepository extends JpaRepository<TrackingDao, Integer> {

}
