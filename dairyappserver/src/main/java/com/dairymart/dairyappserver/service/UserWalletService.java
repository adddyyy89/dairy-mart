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

    public UserWalletDao getOrCreateWallet(int userId) {
        UserWalletDao existing = getWalletDetails(userId);
        if (existing != null) {
            return existing;
        }
        java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
        UserWalletDao wallet = new UserWalletDao();
        wallet.setUserId(userId);
        wallet.setBalance(0);
        wallet.setOutstanding(0);
        wallet.setCreatedOn(now);
        wallet.setLastUpdated(now);
        wallet.setCreatedBy(0);
        return walletRepository.save(wallet);
    }

    public UserWalletDao applyDelta(int userId, double balanceDelta, double outstandingDelta) {
        UserWalletDao wallet = getOrCreateWallet(userId);
        wallet.setBalance(wallet.getBalance() + balanceDelta);
        wallet.setOutstanding(wallet.getOutstanding() + outstandingDelta);
        wallet.setLastUpdated(new java.sql.Date(System.currentTimeMillis()));
        return walletRepository.save(wallet);
    }

    public UserWalletDao addWalletDetails(UserWalletDao walletDao) {
        return walletRepository.save(walletDao);
    }

}
