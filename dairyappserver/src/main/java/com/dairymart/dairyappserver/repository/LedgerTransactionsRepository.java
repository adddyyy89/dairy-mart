package com.dairymart.dairyappserver.repository;

import com.dairymart.dairyappserver.dao.LedgerTransactionsDao;
import com.dairymart.dairyappserver.dao.UserWalletDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerTransactionsRepository extends JpaRepository<LedgerTransactionsDao, Long> {

}
