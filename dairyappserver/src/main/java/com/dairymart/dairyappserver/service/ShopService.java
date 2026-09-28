package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.GSTDao;
import com.dairymart.dairyappserver.dao.ProductDao;
import com.dairymart.dairyappserver.dao.ShopDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dto.ProductDTO;
import com.dairymart.dairyappserver.dto.ShopDTO;
import com.dairymart.dairyappserver.repository.GSTRepository;
import com.dairymart.dairyappserver.repository.ProductRepository;
import com.dairymart.dairyappserver.repository.ShopRepository;
import com.dairymart.dairyappserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private GSTRepository gstRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ShopDao> getAllShops() {
        return shopRepository.findAll();
    }

    @Transactional
    public ShopDao createShop(ShopDao shopDao) {
        Date now = new Date(System.currentTimeMillis());
        if (shopDao.getCreatedOn() == null) {
            shopDao.setCreatedOn(now);
        }
        shopDao.setLastUpdated(now);

        if (shopDao.getAddressId() <= 0 && shopDao.getUserId() > 0) {
            UserDao owner = userRepository.findById(shopDao.getUserId()).orElse(null);
            if (owner != null) {
                shopDao.setAddressId(owner.getAddressId());
            }
        }

        if (shopDao.getGstId() <= 0) {
            GSTDao gst = new GSTDao();
            if (shopDao.getGst() != null) {
                gst.setGstNumber(blankToNa(shopDao.getGst().getGstNumber()));
                gst.setAadharNumber(blankToNa(shopDao.getGst().getAadharNumber()));
                gst.setPanNumber(blankToNa(shopDao.getGst().getPanNumber()));
            } else {
                gst.setGstNumber("NA");
                gst.setAadharNumber("NA");
                gst.setPanNumber("NA");
            }
            gst = persistGst(gst);
            shopDao.setGstId(gst.getGstId());
            shopDao.setGst(null);
        }
        shopDao.setActive(true);

        return shopRepository.save(shopDao);
    }

    private GSTDao persistGst(GSTDao gst) {
        if (gst.getGstId() <= 0) {
            Integer maxId = gstRepository.findMaxGstId();
            gst.setGstId((maxId == null ? 0 : maxId) + 1);
        }
        return gstRepository.save(gst);
    }

    private static String blankToNa(String value) {
        return value == null || value.isBlank() ? "NA" : value.trim();
    }

    public ShopDao findById(int id) {
        Optional<ShopDao> shopDao = shopRepository.findById(id);
        return shopDao.orElse(null);
    }

    public List<ShopDao> findShopByName(String productQuery) {
        //String pNumber = String.valueOf(phoneNumber);
        return shopRepository.findAll().stream().filter(x -> x.getShopName().contains(productQuery)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Transactional
    public ShopDao updateById(ShopDTO dto) {
        ShopDao existing = findById(dto.getShopId());
        if (existing == null) {
            return null;
        }

        if (dto.getShopName() != null && !dto.getShopName().isEmpty()) {
            existing.setShopName(dto.getShopName());
        }
        if (dto.getAddressId() > 0) {
            existing.setAddressId(dto.getAddressId());
        }
        existing.setLastUpdated(new Date(System.currentTimeMillis()));

        if (dto.getGst() != null || dto.getGstId() > 0) {
            int gstId = existing.getGstId() > 0 ? existing.getGstId() : dto.getGstId();
            GSTDao gst = gstId > 0 ? gstRepository.findById(gstId).orElse(null) : null;
            if (gst == null) {
                gst = new GSTDao();
            }
            if (dto.getGst() != null) {
                gst.setGstNumber(blankToNa(dto.getGst().getGstNumber()));
                gst.setPanNumber(blankToNa(dto.getGst().getPanNumber()));
                gst.setAadharNumber(blankToNa(dto.getGst().getAadharNumber()));
            }
            gst = persistGst(gst);
            existing.setGstId(gst.getGstId());
        }
        existing.setGst(null);
        return shopRepository.save(existing);
    }

    public List<ShopDao> getShopByRetailerId(int retailerId) {
        return getAllShops().stream().filter(s -> s.getUserId() == retailerId).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<ShopDao> getAllShopsByAreaName(String queryString) {

        List<ShopDao> shopAddressList = shopRepository.findAll().stream().filter(x -> x.getAddress().getFullAddress().contains(queryString)).collect(Collectors.toCollection(ArrayList::new));
        List<ShopDao> shopCityList = shopRepository.findAll().stream().filter(x -> x.getAddress().getCity().getCityName().contains(queryString)).collect(Collectors.toCollection(ArrayList::new));
        List<ShopDao> shopStateList = shopRepository.findAll().stream().filter(x -> x.getAddress().getCity().getState().getStateName().contains(queryString)).collect(Collectors.toCollection(ArrayList::new));

        List<ShopDao> daoList = new ArrayList<>();
        Map<Integer, ShopDao> daoMap = new HashMap<>();
        for(ShopDao dao : shopAddressList) {
            if(daoMap.get(dao.getAddressId()) == null){
                daoMap.put(dao.getAddressId(), dao);
            }
        }

        for(ShopDao dao : shopCityList) {
            if(daoMap.get(dao.getAddressId()) == null){
                daoMap.put(dao.getAddressId(), dao);
            }
        }

        for(ShopDao dao : shopStateList) {
            if(daoMap.get(dao.getAddressId()) == null){
                daoMap.put(dao.getAddressId(), dao);
            }
        }

        daoList = new ArrayList<>(daoMap.values());

        return daoList;
    }
}
