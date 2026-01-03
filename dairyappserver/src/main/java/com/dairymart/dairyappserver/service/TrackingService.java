package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.BranchDao;
import com.dairymart.dairyappserver.dao.TrackingDao;
import com.dairymart.dairyappserver.dto.BranchDTO;
import com.dairymart.dairyappserver.repository.BranchRepository;
import com.dairymart.dairyappserver.repository.TrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrackingService {

    @Autowired
    private TrackingRepository trackingRepository;

    public TrackingDao updateLocation(TrackingDao trackingDao) {
        return trackingRepository.save(trackingDao);
    }

    public List<TrackingDao> getTrackingLocationsByDate(Date date, int userId) {
        List<TrackingDao> trackingDaos = trackingRepository.findAll().stream().filter(t -> new Date(t.getTimestamp().getTime()).toLocalDate().isEqual(date.toLocalDate()) && t.getUserId() == userId).collect(Collectors.toCollection(ArrayList::new));
        trackingDaos.sort((o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp()));
        return trackingDaos;
    }


}
