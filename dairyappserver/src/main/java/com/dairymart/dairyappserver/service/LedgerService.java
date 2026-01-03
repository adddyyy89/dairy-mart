package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.*;
import com.dairymart.dairyappserver.dto.LedgerTransactionsDTO;
import com.dairymart.dairyappserver.dto.SalesmanLedgerForRetailerDTO;
import com.dairymart.dairyappserver.repository.*;
import com.dairymart.dairyappserver.util.DateUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LedgerService {

    Logger logger = LoggerFactory.getLogger(LedgerService.class);

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private DailyLedgerRepository dailyLedgerRepository;

    @Autowired
    private LedgerTransactionsRepository ledgerTransactionsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserWalletService walletService;

    public List<LedgerTransactionsDao> getSalesmanDashboardTransactions(int salesmanId) {
        List<LedgerDao> ledgerDaos = ledgerRepository.findAll().stream().filter(x -> x.getSalesmanId() == salesmanId).collect(Collectors.toCollection(ArrayList::new));
        logger.info("Fetched all ledgers for salesman: " + salesmanId + ", count = " + ledgerDaos.size());


        List<LedgerTransactionsDao> ledgerTransactionsDaos = new ArrayList<>();
        for(LedgerDao ledgerDao : ledgerDaos) {
            List<LedgerTransactionsDao> ledgerTransactionsDaos1 = ledgerTransactionsRepository.findAll().stream().filter(x -> x.getLedgerId() == ledgerDao.getLedgerId()).collect(Collectors.toCollection(ArrayList::new));
            ledgerTransactionsDaos.addAll(ledgerTransactionsDaos1);
        }

        logger.info("Fetched all ledger transactions for salesman: " + salesmanId + ", count = " + ledgerTransactionsDaos.size());
        ledgerTransactionsDaos.sort((o1, o2) -> o1.getCreatedOn().compareTo(o2.getCreatedOn()));

        return ledgerTransactionsDaos;

    }

    public List<LedgerTransactionsDao> getRetailerDashboardTransactions(int retailerId) {
        List<LedgerDao> ledgerDaos = ledgerRepository.findAll().stream().filter(x -> x.getRetailerId() == retailerId).collect(Collectors.toCollection(ArrayList::new));
        logger.info("Fetched ledger data for retailer : " + retailerId + ", count = " + ledgerDaos.size());


        List<LedgerTransactionsDao> ledgerTransactionsDaos = new ArrayList<>();
        for(LedgerDao ledgerDao : ledgerDaos) {
            List<LedgerTransactionsDao> ledgerTransactionsDaos1 = ledgerTransactionsRepository.findAll().stream().filter(x -> x.getLedgerId() == ledgerDao.getLedgerId()).collect(Collectors.toCollection(ArrayList::new));
            ledgerTransactionsDaos.addAll(ledgerTransactionsDaos1);
        }

        logger.info("Fetched ledger data for retailer : " + retailerId + ", count = " + ledgerTransactionsDaos.size());
        ledgerTransactionsDaos.sort((o1, o2) -> o1.getCreatedOn().compareTo(o2.getCreatedOn()));

        return ledgerTransactionsDaos;

    }

    public Map<LedgerDao, Double> getSalesmanLedgerDetails(int salesmanId) {
        List<LedgerDao> ledgerDaos = ledgerRepository.findAll().stream().filter(x -> x.getSalesmanId() == salesmanId).collect(Collectors.toCollection(ArrayList::new));
        logger.info("Fetched all ledgers for salesman: " + salesmanId + ", count = " + ledgerDaos.size());

        double debitAmt = 0;
        double creditAmt = 0;
        Map<LedgerDao, Double> map  = new HashMap<>();
        for(LedgerDao ledgerDao : ledgerDaos) {
            debitAmt = 0d;
            creditAmt = 0d;
            List<LedgerTransactionsDao> ledgerTransactionsDaos = ledgerTransactionsRepository.findAll().stream().filter(x -> x.getLedgerId() == ledgerDao.getLedgerId()).collect(Collectors.toCollection(ArrayList::new));
            for(LedgerTransactionsDao ledgerTransactionsDao : ledgerTransactionsDaos) {
                if(ledgerTransactionsDao.isDebit()) {
                    debitAmt+=ledgerTransactionsDao.getAmount();
                } else if(ledgerTransactionsDao.isCredit()) {
                    creditAmt+=ledgerTransactionsDao.getAmount();
                }
            }
            map.put(ledgerDao, creditAmt - debitAmt);
        }

        return map;
    }

    // returns list of retailers who are not currently mapped to a salesman
    public List<UserDao> getListToCreateLedger(int salesmanId) {
        List<LedgerDao> alreadyMappedledgerDaos = ledgerRepository.findAll().stream().filter(x -> x.getSalesmanId() == salesmanId).collect(Collectors.toCollection(ArrayList::new));
        List<UserDao> users = userService.getAllUsers();
        List<UserDao> finalRetailerList = new ArrayList<>();
        for(UserDao user : users) {
            boolean isMapped = false;
            for(LedgerDao ledgerDao : alreadyMappedledgerDaos) {
                if(ledgerDao.getRetailerId() == user.getUserId() && user.getUserId() != salesmanId) {
                    isMapped = true;
                    break;
                }
            }
            if(!isMapped) {
                finalRetailerList.add(user);
            }
        }
        return finalRetailerList;
    }

    public LedgerDao createLedger(LedgerDao ledgerDao) {
        return ledgerRepository.save(ledgerDao);
    }

    public LedgerDao getLedger(long ledgerId) {
        return ledgerRepository.findById(ledgerId).orElse(null);
    }

    public SalesmanLedgerForRetailerDTO getLedgerBalanceBySalesman(long ledgerId) {
        List<LedgerTransactionsDao> ledgerTransactionsDaos = ledgerTransactionsRepository.findAll().stream().filter(t -> t.getLedgerId() == ledgerId).collect(Collectors.toCollection(ArrayList::new));
        ledgerTransactionsDaos.sort((o1, o2) -> o1.getCreatedOn().compareTo(o2.getCreatedOn()));

        double balance = 0d;
        double credit = 0d;
        double debit = 0d;

        ArrayList<LedgerTransactionsDTO> ledgerTransactionsDTOS = new ArrayList<>();
        for(LedgerTransactionsDao ledgerTransactionsDao : ledgerTransactionsDaos) {
            if(ledgerTransactionsDao.isDebit()) {
                debit+=ledgerTransactionsDao.getAmount();
            }
            if(ledgerTransactionsDao.isCredit()) {
                credit+=ledgerTransactionsDao.getAmount();
            }
            ledgerTransactionsDTOS.add(new LedgerTransactionsDTO(ledgerTransactionsDao));
        }
        balance = credit - debit;

        UserDao retailer = userService.findById(getLedger(ledgerId).getRetailerId());

        SalesmanLedgerForRetailerDTO salesmanLedgerForRetailerDTO = new SalesmanLedgerForRetailerDTO();
        salesmanLedgerForRetailerDTO.setRetailerAddress(retailer.getAddress().getFullAddress());
        salesmanLedgerForRetailerDTO.setBalance(balance);
        salesmanLedgerForRetailerDTO.setTransactionsDTOS(ledgerTransactionsDTOS);
        salesmanLedgerForRetailerDTO.setRetailerName(retailer.getFirstName() + " " + retailer.getLastName());

        return salesmanLedgerForRetailerDTO;
    }

    @Transactional
    public LedgerTransactionsDao updateSalesmanLedgerTransaction(LedgerTransactionsDao ledgerTransactionsDao) {
        LedgerTransactionsDao ledgerTransaction = ledgerTransactionsRepository.save(ledgerTransactionsDao);

        LedgerDao ledgerDao = getLedger(ledgerTransaction.getLedgerId());

        // Update wallet of receiver and payee
        UserWalletDao payeeWallet = new UserWalletDao();
        UserWalletDao receiverWallet = new UserWalletDao();
        int payeeUserId = 0;
        int receiverUserId = 0;

        if(ledgerTransaction.isCredit()) {
            receiverUserId = ledgerDao.getSalesmanId();
            payeeUserId = ledgerDao.getRetailerId();
        } else if(ledgerTransaction.isDebit()) {
            payeeUserId = ledgerDao.getSalesmanId();
            receiverUserId = ledgerDao.getRetailerId();
        }

        payeeWallet = walletService.getWalletDetails(payeeUserId);
        receiverWallet = walletService.getWalletDetails(receiverUserId);

        // Update Payee
        payeeWallet.setBalance(payeeWallet.getBalance() - ledgerTransaction.getAmount());
        payeeWallet.setLastUpdated(new Date(System.currentTimeMillis()));
        payeeWallet.setOutstanding(payeeWallet.getOutstanding() - ledgerTransaction.getAmount());
        walletService.addWalletDetails(payeeWallet);

        // Update Receiver
        receiverWallet.setBalance(receiverWallet.getBalance() + ledgerTransaction.getAmount());
        receiverWallet.setLastUpdated(new Date(System.currentTimeMillis()));
        receiverWallet.setOutstanding(receiverWallet.getOutstanding() - ledgerTransaction.getAmount());
        walletService.addWalletDetails(receiverWallet);

        return ledgerTransaction;
    }

    public DailyLedgerDao getLastDailyLedger(int userId) {
        List<DailyLedgerDao> dailyLedgerDaos = dailyLedgerRepository.findAll().stream().filter(d -> d.getUserId() == userId).collect(Collectors.toCollection(ArrayList::new));
        if(dailyLedgerDaos.size() > 0) {
            dailyLedgerDaos.sort((o1, o2) -> o1.getRecordTimestamp().compareTo(o2.getRecordTimestamp()));
            return dailyLedgerDaos.get(0);
        }
        return null;
    }

    @Transactional
    public void updateDailyLedger() {
        logger.info("=========== Daily Ledger Update started ===========");
        Timestamp todayTimestamp = new Timestamp(System.currentTimeMillis());

        List<LedgerTransactionsDao> ledgerTransactionsDaos = ledgerTransactionsRepository.findAll().stream().filter(t -> DateUtil.isYesterday(t.getCreatedOn())).collect(Collectors.toCollection(ArrayList::new));
        logger.info("Total previous day transactions = " + ledgerTransactionsDaos.size());

        Map<Integer, List<LedgerTransactionsDao>> creditTransansactionsMap = new HashMap<>();
        Map<Integer, List<LedgerTransactionsDao>> debitTransansactionsMap = new HashMap<>();

        for(LedgerTransactionsDao ledgerTransactionsDao : ledgerTransactionsDaos) {

            LedgerDao ledgerDao = getLedger(ledgerTransactionsDao.getLedgerId());

            int payeeId = ledgerDao.getSalesmanId();
            int receiverId = ledgerDao.getRetailerId();

            if(ledgerTransactionsDao.isCredit()) {
                payeeId = ledgerDao.getRetailerId();
                receiverId = ledgerDao.getSalesmanId();
            }

            // Update credit map with user and his credited transactions
            if(creditTransansactionsMap.containsKey(receiverId)) {
                creditTransansactionsMap.get(receiverId).add(ledgerTransactionsDao);
            }
            else {
                List<LedgerTransactionsDao> ledgerTransactionsDaoList = new ArrayList<>();
                ledgerTransactionsDaoList.add(ledgerTransactionsDao);
                creditTransansactionsMap.put(receiverId, ledgerTransactionsDaoList);
            }

            // Update debit map with user and his debited transactions
            if(debitTransansactionsMap.containsKey(receiverId)) {
                debitTransansactionsMap.get(receiverId).add(ledgerTransactionsDao);
            }
            else {
                List<LedgerTransactionsDao> ledgerTransactionsDaoList = new ArrayList<>();
                ledgerTransactionsDaoList.add(ledgerTransactionsDao);
                debitTransansactionsMap.put(receiverId, ledgerTransactionsDaoList);
            }
        }

        logger.info("Total credit transactions = " + creditTransansactionsMap.size());
        logger.info("Total debited transactions = " + debitTransansactionsMap.size());

        // Use this to add the processed users
        List<Integer> processedUsers = new ArrayList<>();

        // For all credited transactions and if debited transactions for each user
        for(Map.Entry<Integer, List<LedgerTransactionsDao>> entrySet : creditTransansactionsMap.entrySet()) {
            int userId = entrySet.getKey();

            if(!processedUsers.contains(userId)) {
                List<LedgerTransactionsDao> creditedTransactions = entrySet.getValue();
                List<LedgerTransactionsDao> debitedTransactions = debitTransansactionsMap.get(userId);

                DailyLedgerDao lastDailyLedger = getLastDailyLedger(userId);
                // Get starting wallet balance & starting outstanding balance
                double prevWalletBalance = lastDailyLedger.getWalletBalance();
                double prevOutstandingBalance = lastDailyLedger.getOutstandingBalance();

                double creditAmt = 0d;
                double debitAmt = 0d;
                for (LedgerTransactionsDao creditedTransaction : creditedTransactions) {
                    creditAmt += creditedTransaction.getAmount();
                }

                for (LedgerTransactionsDao debitedTransaction : debitedTransactions) {
                    debitAmt += debitedTransaction.getAmount();
                }

                double walletBalance = prevWalletBalance + creditAmt - debitAmt;
                double outstandingBalance = prevOutstandingBalance + creditAmt - debitAmt;
                double totalBalance = walletBalance - outstandingBalance;

                DailyLedgerDao dailyLedgerDao = new DailyLedgerDao();
                dailyLedgerDao.setCreatedBy(0);
                dailyLedgerDao.setLastUpdated(new Timestamp(System.currentTimeMillis()));
                dailyLedgerDao.setRecordTimestamp(new Timestamp(System.currentTimeMillis()));
                dailyLedgerDao.setUserId(userId);
                dailyLedgerDao.setOutstandingBalance(outstandingBalance);
                dailyLedgerDao.setStartingWalletBalance(prevWalletBalance);
                dailyLedgerDao.setTotalBalance(totalBalance);
                dailyLedgerDao.setWalletBalance(walletBalance);
                dailyLedgerDao.setWalletId(lastDailyLedger.getWalletId());
                dailyLedgerDao.setStartingOutstandingBalance(prevOutstandingBalance);

                dailyLedgerRepository.save(dailyLedgerDao);

                processedUsers.add(userId);
            }
        }

        // for all debited transactions for users
        for(Map.Entry<Integer, List<LedgerTransactionsDao>> entrySet : debitTransansactionsMap.entrySet()) {
            int userId = entrySet.getKey();

            if(!processedUsers.contains(userId)) {
                List<LedgerTransactionsDao> debitedTransactions = entrySet.getValue();

                DailyLedgerDao lastDailyLedger = getLastDailyLedger(userId);
                // Get starting wallet balance & starting outstanding balance
                double prevWalletBalance = lastDailyLedger.getWalletBalance();
                double prevOutstandingBalance = lastDailyLedger.getOutstandingBalance();

                double debitAmt = 0d;

                for (LedgerTransactionsDao debitedTransaction : debitedTransactions) {
                    debitAmt += debitedTransaction.getAmount();
                }

                double walletBalance = prevWalletBalance - debitAmt;
                double outstandingBalance = prevOutstandingBalance - debitAmt;
                double totalBalance = walletBalance - outstandingBalance;

                DailyLedgerDao dailyLedgerDao = new DailyLedgerDao();
                dailyLedgerDao.setCreatedBy(0);
                dailyLedgerDao.setLastUpdated(new Timestamp(System.currentTimeMillis()));
                dailyLedgerDao.setRecordTimestamp(new Timestamp(System.currentTimeMillis()));
                dailyLedgerDao.setUserId(userId);
                dailyLedgerDao.setOutstandingBalance(outstandingBalance);
                dailyLedgerDao.setStartingWalletBalance(prevWalletBalance);
                dailyLedgerDao.setTotalBalance(totalBalance);
                dailyLedgerDao.setWalletBalance(walletBalance);
                dailyLedgerDao.setWalletId(lastDailyLedger.getWalletId());
                dailyLedgerDao.setStartingOutstandingBalance(prevOutstandingBalance);

                dailyLedgerRepository.save(dailyLedgerDao);

                processedUsers.add(userId);
            }
        }

        logger.info("Daily transactions updated for user id: " + processedUsers);
        logger.info("=========== Daily Ledger Update completed ===========");
    }

    public List<LedgerDao> getAllLedgers() {
        return ledgerRepository.findAll();
    }

    public List<LedgerTransactionsDao> getAllLedgerTransactions() {
        return ledgerTransactionsRepository.findAll();
    }

    public List<LedgerTransactionsDao> getTodaysLedgerTransactions() {
        return ledgerTransactionsRepository.findAll().stream().filter(x -> DateUtil.isSameDay(new Timestamp(x.getCreatedOn().getTime()))).collect(Collectors.toCollection(ArrayList::new));
    }

}
