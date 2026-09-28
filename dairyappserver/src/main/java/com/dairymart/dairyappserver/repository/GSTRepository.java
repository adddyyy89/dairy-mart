package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.GSTDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GSTRepository extends JpaRepository<GSTDao, Integer> {

    @Query(value = "SELECT COALESCE(MAX(gstid), 0) FROM public.gst", nativeQuery = true)
    Integer findMaxGstId();
}
