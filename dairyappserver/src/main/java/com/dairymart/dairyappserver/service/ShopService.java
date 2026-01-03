package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.ProductDao;
import com.dairymart.dairyappserver.dao.ShopDao;
import com.dairymart.dairyappserver.dto.ProductDTO;
import com.dairymart.dairyappserver.dto.ShopDTO;
import com.dairymart.dairyappserver.repository.ProductRepository;
import com.dairymart.dairyappserver.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    public List<ShopDao> getAllShops() {
        return shopRepository.findAll();
    }

    public ShopDao createShop(ShopDao shopDao) {
        return shopRepository.save(shopDao);
    }

    public ShopDao findById(int id) {
        Optional<ShopDao> shopDao = shopRepository.findById(id);
        return shopDao.orElse(null);
    }

    public List<ShopDao> findShopByName(String productQuery) {
        //String pNumber = String.valueOf(phoneNumber);
        return shopRepository.findAll().stream().filter(x -> x.getShopName().contains(productQuery)).collect(Collectors.toCollection(ArrayList::new));
    }

    public ShopDao updateById(ShopDTO dto) {

        int pId = dto.getShopId();
        ShopDao d = findById(pId);
        if(d == null) {
            return null;
        }

        ShopDao dao = new ShopDao(dto);
        dao.setLastUpdated(new Date(System.currentTimeMillis()));
        dao.setShopId(dto.getShopId());
        return shopRepository.save(dao);


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
