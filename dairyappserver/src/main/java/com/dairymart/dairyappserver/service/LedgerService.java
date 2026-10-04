package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.*;
import com.dairymart.dairyappserver.dto.LedgerTransactionsDTO;
import com.dairymart.dairyappserver.dto.SalesmanLedgerForRetailerDTO;
import com.dairymart.dairyappserver.dto.SalesmanWalletSummaryDTO;
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

    @Autowired
    private ShopService shopService;

    @Autowired
    private SalesmanToRetailService salesmanToRetailService;

    @Autowired
    private NotificationService notificationService;

    public List<LedgerTransactionsDao> getSalesmanDashboardTransactions(int salesmanId) {
        List<LedgerDao> ledgerDaos = ledgerRepository.findAll().stream().filter(x -> x.getSalesmanId() == salesmanId).collect(Collectors.toCollection(ArrayList::new));
        logger.info("Fetched all ledgers for salesman: " + salesmanId + ", count = " + ledgerDaos.size());


        List<LedgerTransactionsDao> ledgerTransactionsDaos = new ArrayList<>();
        for(LedgerDao ledgerDao : ledgerDaos) {
            List<LedgerTransactionsDao> ledgerTransactionsDaos1 = ledgerTransactionsRepository.findAll().stream().filter(x -> x.getLedgerId() == ledgerDao.getLedgerId()).collect(Collectors.toCollection(ArrayList::new));
            ledgerTransactionsDaos.addAll(ledgerTransactionsDaos1);
        }

        logger.info("Fetched all ledger transactions for salesman: " + salesmanId + ", count = " + ledgerTransactionsDaos.size());
        ledgerTransactionsDaos.sort(this::compareCreatedOn);

        return ledgerTransactionsDaos;

    }

    public List<LedgerTransactionsDao> getRetailerDashboardTransactions(int retailerId) {
        List<Integer> shopIds = shopService.getShopByRetailerId(retailerId).stream()
                .map(ShopDao::getShopId)
                .collect(Collectors.toList());
        List<LedgerDao> ledgerDaos = ledgerRepository.findAll().stream()
                .filter(x -> x.getRetailerId() == retailerId || shopIds.contains(x.getRetailerId()))
                .collect(Collectors.toCollection(ArrayList::new));
        logger.info("Fetched ledger data for retailer : " + retailerId + ", count = " + ledgerDaos.size());


        List<LedgerTransactionsDao> ledgerTransactionsDaos = new ArrayList<>();
        for(LedgerDao ledgerDao : ledgerDaos) {
            List<LedgerTransactionsDao> ledgerTransactionsDaos1 = ledgerTransactionsRepository.findAll().stream().filter(x -> x.getLedgerId() == ledgerDao.getLedgerId()).collect(Collectors.toCollection(ArrayList::new));
            ledgerTransactionsDaos.addAll(ledgerTransactionsDaos1);
        }

        logger.info("Fetched ledger data for retailer : " + retailerId + ", count = " + ledgerTransactionsDaos.size());
        ledgerTransactionsDaos.sort(this::compareCreatedOn);

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
        ledgerTransactionsDaos.sort(this::compareCreatedOn);

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

        UserDao retailer = resolveRetailerUser(getLedger(ledgerId).getRetailerId());

        SalesmanLedgerForRetailerDTO salesmanLedgerForRetailerDTO = new SalesmanLedgerForRetailerDTO();
        salesmanLedgerForRetailerDTO.setBalance(balance);
        salesmanLedgerForRetailerDTO.setTransactionsDTOS(ledgerTransactionsDTOS);
        if (retailer != null) {
            salesmanLedgerForRetailerDTO.setRetailerName(retailer.getFirstName() + " " + retailer.getLastName());
            if (retailer.getAddress() != null) {
                salesmanLedgerForRetailerDTO.setRetailerAddress(retailer.getAddress().getFullAddress());
            }
        } else {
            salesmanLedgerForRetailerDTO.setRetailerName("Retailer");
            salesmanLedgerForRetailerDTO.setRetailerAddress("");
        }

        return salesmanLedgerForRetailerDTO;
    }

    /**
     * Salesman records an offline payment (cash/UPI) from the retailer.
     * Credit: salesman wallet increases, retailer pending (negative wallet) is reduced.
     * Debit: treated as an extra charge on the retailer (same as an order).
     */
    @Transactional
    public LedgerTransactionsDao updateSalesmanLedgerTransaction(LedgerTransactionsDao ledgerTransactionsDao) {
        if (ledgerTransactionsDao.getPaymentTypeId() <= 0) {
            ledgerTransactionsDao.setPaymentTypeId(1);
        }
        if (ledgerTransactionsDao.getCreatedOn() == null) {
            ledgerTransactionsDao.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        }
        if (ledgerTransactionsDao.getLastUpdated() == null) {
            ledgerTransactionsDao.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        }
        if (ledgerTransactionsDao.getTransactionId() == 0) {
            ledgerTransactionsDao.setTransactionsId(0);
        }

        LedgerDao ledgerDao = getLedger(ledgerTransactionsDao.getLedgerId());
        if (ledgerDao == null) {
            throw new IllegalArgumentException("Ledger not found");
        }

        LedgerTransactionsDao ledgerTransaction = ledgerTransactionsRepository.save(ledgerTransactionsDao);
        int retailerUserId = userIdForLedgerParty(ledgerDao.getRetailerId());
        double amount = ledgerTransaction.getAmount();

        if (ledgerTransaction.isCredit()) {
            walletService.applyDelta(ledgerDao.getSalesmanId(), amount, 0);
            walletService.applyDelta(retailerUserId, amount, -amount);
        } else if (ledgerTransaction.isDebit()) {
            walletService.applyDelta(retailerUserId, -amount, amount);
        }
        try {
            notificationService.notifyLedgerTransaction(
                    ledgerDao.getSalesmanId(), retailerUserId, ledgerTransaction.isCredit(),
                    amount, ledgerTransaction.getTransactionId());
        } catch (Exception ex) {
            logger.error("Failed to notify ledger transaction {}", ledgerTransaction.getTransactionId(), ex);
        }
        logger.info("Ledger tx {} ledger={} credit={} amount={}",
                ledgerTransaction.getTransactionId(), ledgerDao.getLedgerId(),
                ledgerTransaction.isCredit(), amount);
        return ledgerTransaction;
    }

    /**
     * When an order leaves NEW, debit the retailer ledger and wallet (can go negative).
     * Salesman wallet is unchanged until they record a collection.
     */
    @Transactional
    public void postOrderCharge(RetailOrderDao order) {
        if (order == null) {
            return;
        }
        double amount = orderAmount(order);
        if (amount <= 0) {
            logger.warn("Skipping ledger post for order {} — amount is {}", order.getOrderId(), amount);
            return;
        }
        int shopId = order.getRetailerId();
        int retailerUserId = userIdForShop(shopId);
        Integer salesmanId = resolveSalesmanUserId(shopId, retailerUserId);
        if (salesmanId == null) {
            logger.error("No salesman mapping for shop {} / retailer user {} — cannot post order {} to ledger",
                    shopId, retailerUserId, order.getOrderId());
            return;
        }

        LedgerDao ledger = findOrCreateLedger(salesmanId, retailerUserId, order.getCreatedBy());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        LedgerTransactionsDao tx = new LedgerTransactionsDao();
        tx.setLedgerId(ledger.getLedgerId());
        tx.setAmount(amount);
        tx.setDebit(true);
        tx.setCredit(false);
        tx.setPaymentTypeId(1);
        tx.setCreatedOn(now);
        tx.setLastUpdated(now);
        tx.setCreatedBy(order.getCreatedBy());
        ledgerTransactionsRepository.save(tx);

        walletService.applyDelta(retailerUserId, -amount, amount);
        try {
            notificationService.notifyLedgerTransaction(salesmanId, retailerUserId, false, amount, tx.getTransactionId());
        } catch (Exception ex) {
            logger.error("Failed to notify order ledger charge for order {}", order.getOrderId(), ex);
        }
        logger.info("Posted order {} amount {} to ledger {} for retailer {}",
                order.getOrderId(), amount, ledger.getLedgerId(), retailerUserId);
    }

    public LedgerDao findOrCreateLedger(int salesmanId, int retailerUserId, int createdBy) {
        for (LedgerDao existing : ledgerRepository.findAll()) {
            if (existing.getSalesmanId() != salesmanId) {
                continue;
            }
            if (existing.getRetailerId() == retailerUserId) {
                return existing;
            }
            ShopDao shop = shopService.findById(existing.getRetailerId());
            if (shop != null && shop.getUserId() == retailerUserId) {
                return existing;
            }
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        LedgerDao ledger = new LedgerDao();
        ledger.setSalesmanId(salesmanId);
        ledger.setRetailerId(retailerUserId);
        ledger.setActive(true);
        ledger.setCreatedBy(createdBy);
        ledger.setCreatedOn(now);
        ledger.setLastUpdated(now);
        return ledgerRepository.save(ledger);
    }

    private int userIdForShop(int shopId) {
        ShopDao shop = shopService.findById(shopId);
        if (shop != null && shop.getUserId() > 0) {
            return shop.getUserId();
        }
        return shopId;
    }

    private int userIdForLedgerParty(int retailerOrShopId) {
        UserDao user = userService.findById(retailerOrShopId);
        if (user != null) {
            return user.getUserId();
        }
        return userIdForShop(retailerOrShopId);
    }

    private UserDao resolveRetailerUser(int retailerOrShopId) {
        return userService.findById(userIdForLedgerParty(retailerOrShopId));
    }

    private Integer resolveSalesmanUserId(int shopId, int retailerUserId) {
        List<SalesmanToRetailDao> maps = salesmanToRetailService.getAllSalestoRetail();
        for (SalesmanToRetailDao map : maps) {
            if (Boolean.FALSE.equals(map.isActive())) {
                continue;
            }
            if (map.getRetailerId() == shopId || map.getRetailerId() == retailerUserId) {
                return map.getSalesmanId();
            }
        }
        for (SalesmanToRetailDao map : maps) {
            if (map.getRetailerId() == shopId || map.getRetailerId() == retailerUserId) {
                return map.getSalesmanId();
            }
        }
        return null;
    }

    private double orderAmount(RetailOrderDao order) {
        List<RetailOrderDetailsDao> details = order.getOrderDetails();
        if (details == null || details.isEmpty()) {
            return 0;
        }
        double total = 0;
        for (RetailOrderDetailsDao line : details) {
            double qty = parseNumber(line.getQuantity());
            double rate = parseNumber(line.getSaleRate());
            total += qty * rate;
        }
        return total;
    }

    private double parseNumber(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int compareCreatedOn(LedgerTransactionsDao a, LedgerTransactionsDao b) {
        Timestamp ta = a.getCreatedOn();
        Timestamp tb = b.getCreatedOn();
        if (ta == null && tb == null) {
            return 0;
        }
        if (ta == null) {
            return 1;
        }
        if (tb == null) {
            return -1;
        }
        return ta.compareTo(tb);
    }

    public List<SalesmanWalletSummaryDTO> getSalesmanWalletSummaries() {
        List<SalesmanWalletSummaryDTO> summaries = new ArrayList<>();
        for (UserDao salesman : userService.findByTypeId(2)) {
            summaries.add(getSalesmanWalletSummary(salesman));
        }
        return summaries;
    }

    public SalesmanWalletSummaryDTO getSalesmanWalletSummary(UserDao salesman) {
        int salesmanId = salesman.getUserId();
        UserWalletDao wallet = walletService.getOrCreateWallet(salesmanId);
        double received = 0;
        double pending = 0;
        Map<LedgerDao, Double> ledgers = getSalesmanLedgerDetails(salesmanId);
        Set<Long> ledgerIds = ledgers.keySet().stream().map(LedgerDao::getLedgerId).collect(Collectors.toSet());
        List<LedgerTransactionsDao> txs = ledgerTransactionsRepository.findAll().stream()
                .filter(t -> ledgerIds.contains(t.getLedgerId()))
                .collect(Collectors.toList());
        for (LedgerTransactionsDao tx : txs) {
            if (tx.isCredit()) {
                received += tx.getAmount();
            }
        }
        for (double net : ledgers.values()) {
            if (net < 0) {
                pending += -net;
            }
        }
        SalesmanWalletSummaryDTO dto = new SalesmanWalletSummaryDTO();
        dto.setUserId(salesmanId);
        dto.setName((salesman.getFirstName() == null ? "" : salesman.getFirstName()) + " "
                + (salesman.getLastName() == null ? "" : salesman.getLastName()));
        dto.setPhoneNumber(salesman.getPhoneNumber());
        dto.setWalletBalance(wallet.getBalance());
        dto.setOutstanding(wallet.getOutstanding());
        dto.setReceived(received);
        dto.setPending(pending);
        return dto;
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
