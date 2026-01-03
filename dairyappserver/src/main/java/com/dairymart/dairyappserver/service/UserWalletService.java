package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.CrateDao;
import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dao.UserWalletDao;
import com.dairymart.dairyappserver.dto.UserDTO;
import com.dairymart.dairyappserver.repository.CrateRepository;
import com.dairymart.dairyappserver.repository.UserWalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserWalletService {

    Logger logger = LoggerFactory.getLogger(UserWalletService.class);

    @Autowired
    private UserWalletRepository walletRepository;

    public UserWalletDao getWalletDetails(int userId) {
        List<UserWalletDao> wallets = walletRepository.findAll().stream().filter(wallet->wallet.getUserId()==userId).collect(Collectors.toCollection(ArrayList::new));

        return wallets.isEmpty() ? null : wallets.get(0);
    }

    public UserWalletDao addWalletDetails(UserWalletDao walletDao) {
        return walletRepository.save(walletDao);
    }

}
