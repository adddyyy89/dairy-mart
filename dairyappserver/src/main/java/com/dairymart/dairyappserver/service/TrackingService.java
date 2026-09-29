package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.TrackingDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.LiveLocationDTO;
import com.dairymart.dairyappserver.repository.TrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrackingService {

    @Autowired
    private TrackingRepository trackingRepository;

    @Autowired
    private UserService userService;

    public TrackingDao updateLocation(TrackingDao trackingDao) {
        return trackingRepository.save(trackingDao);
    }

    public List<TrackingDao> getTrackingLocationsByDate(Date date, int userId) {
        List<TrackingDao> trackingDaos = trackingRepository.findAll().stream().filter(t -> t.getTimestamp() != null
                && new Date(t.getTimestamp().getTime()).toLocalDate().isEqual(date.toLocalDate())
                && t.getUserId() == userId).collect(Collectors.toCollection(ArrayList::new));
        trackingDaos.sort((o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp()));
        return trackingDaos;
    }

    public List<LiveLocationDTO> getLiveSalesmen() {
        Map<Integer, TrackingDao> latest = new HashMap<>();
        for (TrackingDao row : trackingRepository.findAll()) {
            if (row.getLatitude() == null || row.getLongitude() == null || row.getTimestamp() == null) {
                continue;
            }
            TrackingDao prev = latest.get(row.getUserId());
            if (prev == null || row.getTimestamp().after(prev.getTimestamp())) {
                latest.put(row.getUserId(), row);
            }
        }
        long now = System.currentTimeMillis();
        List<LiveLocationDTO> result = new ArrayList<>();
        for (UserDao salesman : userService.findByTypeId(2)) {
            TrackingDao ping = latest.get(salesman.getUserId());
            if (ping == null) {
                continue;
            }
            LiveLocationDTO dto = new LiveLocationDTO();
            dto.setUserId(salesman.getUserId());
            dto.setName(NotificationService.personName(salesman));
            dto.setPhoneNumber(salesman.getPhoneNumber());
            dto.setLatitude(ping.getLatitude());
            dto.setLongitude(ping.getLongitude());
            Timestamp ts = ping.getTimestamp();
            dto.setTimestamp(ts.toString());
            long minutes = Math.max(0, (now - ts.getTime()) / 60000L);
            dto.setMinutesAgo(minutes);
            dto.setStale(minutes > 15);
            result.add(dto);
        }
        result.sort(Comparator.comparing(LiveLocationDTO::getMinutesAgo));
        return result;
    }
}
