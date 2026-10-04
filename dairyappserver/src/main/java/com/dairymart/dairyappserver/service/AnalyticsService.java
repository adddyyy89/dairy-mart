package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.*;
import com.dairymart.dairyappserver.dto.AnalyticsDTO;
import com.dairymart.dairyappserver.dto.AnalyticsRowDTO;
import com.dairymart.dairyappserver.dto.CrateHolderDTO;
import com.dairymart.dairyappserver.dto.CrateSummaryDTO;
import com.dairymart.dairyappserver.repository.LedgerRepository;
import com.dairymart.dairyappserver.repository.LedgerTransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private static final int REJECTED = 3;
    private static final int LIMIT = 20;

    @Autowired
    private RetailOrderService retailOrderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;

    @Autowired
    private CrateService crateService;

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private LedgerTransactionsRepository ledgerTransactionsRepository;

    public AnalyticsDTO build() {
        Map<Integer, ShopDao> shopById = new HashMap<>();
        Map<Integer, ShopDao> shopByUser = new HashMap<>();
        for (ShopDao shop : shopService.getAllShops()) {
            shopById.put(shop.getShopId(), shop);
            shopByUser.putIfAbsent(shop.getUserId(), shop);
        }
        Map<Integer, UserDao> users = new HashMap<>();
        for (UserDao user : userService.getAllUsers()) {
            users.put(user.getUserId(), user);
        }
        Map<String, ProductDao> products = new HashMap<>();
        for (ProductDao product : productService.getAllProducts()) {
            if (product.getProductCode() != null) {
                products.put(product.getProductCode().trim().toUpperCase(), product);
            }
        }

        Map<String, ProductAgg> byProduct = new HashMap<>();
        Map<Integer, StoreAgg> byStore = new HashMap<>();
        Map<String, PairAgg> byPair = new HashMap<>();
        int totalOrders = 0;
        long totalUnits = 0;

        for (RetailOrderDao order : retailOrderService.getAllOrders()) {
            if (order.getOrderStatusId() == REJECTED) {
                continue;
            }
            totalOrders++;
            StoreAgg store = byStore.computeIfAbsent(order.getRetailerId(), id -> new StoreAgg());
            store.orders++;
            if (order.getOrderDetails() == null) {
                continue;
            }
            for (RetailOrderDetailsDao line : order.getOrderDetails()) {
                long qty = parseQty(line.getQuantity());
                double rate = parseMoney(line.getSaleRate());
                double lineAmt = qty * rate;
                store.units += qty;
                totalUnits += qty;

                String code = line.getProductCode() == null ? "" : line.getProductCode().trim().toUpperCase();
                ProductAgg product = byProduct.computeIfAbsent(code, k -> new ProductAgg());
                product.qty += qty;
                product.amount += lineAmt;
                product.byStore.merge(order.getRetailerId(), qty, Long::sum);

                String pairKey = order.getRetailerId() + "|" + code;
                PairAgg pair = byPair.computeIfAbsent(pairKey, k -> new PairAgg(order.getRetailerId(), code));
                pair.qty += qty;
                pair.orders++;
            }
        }

        Map<Integer, Double> salesmanCollected = new HashMap<>();
        Map<Integer, Double> storePaid = new HashMap<>();
        Map<Long, LedgerDao> ledgers = new HashMap<>();
        for (LedgerDao ledger : ledgerRepository.findAll()) {
            ledgers.put(ledger.getLedgerId(), ledger);
        }
        for (LedgerTransactionsDao tx : ledgerTransactionsRepository.findAll()) {
            if (!tx.isCredit()) {
                continue;
            }
            LedgerDao ledger = ledgers.get(tx.getLedgerId());
            if (ledger == null) {
                continue;
            }
            salesmanCollected.merge(ledger.getSalesmanId(), tx.getAmount(), Double::sum);
            storePaid.merge(ledger.getRetailerId(), tx.getAmount(), Double::sum);
        }

        AnalyticsDTO dto = new AnalyticsDTO();
        dto.setTotalOrders(totalOrders);
        dto.setTotalUnits(totalUnits);
        dto.setTotalCollected(salesmanCollected.values().stream().mapToDouble(Double::doubleValue).sum());

        List<AnalyticsRowDTO> topProducts = new ArrayList<>();
        byProduct.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue().qty, a.getValue().qty))
                .limit(LIMIT)
                .forEach(e -> {
                    ProductAgg agg = e.getValue();
                    int topStoreId = 0;
                    long topQty = 0;
                    for (Map.Entry<Integer, Long> store : agg.byStore.entrySet()) {
                        if (store.getValue() > topQty) {
                            topQty = store.getValue();
                            topStoreId = store.getKey();
                        }
                    }
                    topProducts.add(new AnalyticsRowDTO(
                            productName(products, e.getKey()),
                            "Most from " + storeLabel(topStoreId, shopById, shopByUser, users)
                                    + " (" + topQty + " units)",
                            agg.qty,
                            agg.amount));
                });
        dto.setTopProducts(topProducts);

        List<AnalyticsRowDTO> storesByOrders = new ArrayList<>();
        byStore.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().orders, a.getValue().orders))
                .limit(LIMIT)
                .forEach(e -> storesByOrders.add(new AnalyticsRowDTO(
                        storeLabel(e.getKey(), shopById, shopByUser, users),
                        e.getValue().units + " units",
                        e.getValue().orders,
                        0)));
        dto.setStoresByOrders(storesByOrders);

        List<AnalyticsRowDTO> salesmen = new ArrayList<>();
        salesmanCollected.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(LIMIT)
                .forEach(e -> salesmen.add(new AnalyticsRowDTO(
                        personName(users.get(e.getKey()), e.getKey()),
                        "Collected from stores",
                        0,
                        e.getValue())));
        dto.setSalesmenCollected(salesmen);

        List<AnalyticsRowDTO> paid = new ArrayList<>();
        storePaid.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(LIMIT)
                .forEach(e -> paid.add(new AnalyticsRowDTO(
                        storeLabel(e.getKey(), shopById, shopByUser, users),
                        "Paid to salesman",
                        0,
                        e.getValue())));
        dto.setStoresPaid(paid);

        List<AnalyticsRowDTO> storeProducts = new ArrayList<>();
        byPair.values().stream()
                .sorted(Comparator.comparingLong((PairAgg p) -> p.qty).reversed())
                .limit(LIMIT)
                .forEach(p -> storeProducts.add(new AnalyticsRowDTO(
                        storeLabel(p.storeId, shopById, shopByUser, users),
                        productName(products, p.code),
                        p.qty,
                        p.orders)));
        dto.setStoreProducts(storeProducts);

        CrateSummaryDTO crates = crateService.getSummary();
        dto.setCratesInSystem(crates.getTotalInSystem());
        List<AnalyticsRowDTO> locations = new ArrayList<>();
        locations.add(new AnalyticsRowDTO("Branch", "Available at branch", crates.getAtBranch(), 0));
        for (CrateHolderDTO row : crates.getSalesmen()) {
            if (row.getHolding() > 0) {
                locations.add(new AnalyticsRowDTO(row.getName(), "With salesman", row.getHolding(), 0));
            }
        }
        for (CrateHolderDTO row : crates.getStores()) {
            if (row.getAtStore() > 0) {
                locations.add(new AnalyticsRowDTO(row.getName(), "At store", row.getAtStore(), 0));
            }
        }
        locations.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        dto.setCrateLocations(locations);
        return dto;
    }

    private String productName(Map<String, ProductDao> products, String code) {
        ProductDao product = products.get(code);
        if (product != null && product.getProductName() != null && !product.getProductName().isBlank()) {
            return product.getProductName();
        }
        return code.isBlank() ? "Unknown product" : code;
    }

    private String storeLabel(int id, Map<Integer, ShopDao> shopById, Map<Integer, ShopDao> shopByUser,
                              Map<Integer, UserDao> users) {
        ShopDao shop = shopById.get(id);
        if (shop == null) {
            shop = shopByUser.get(id);
        }
        if (shop != null && shop.getShopName() != null && !shop.getShopName().isBlank()) {
            return shop.getShopName();
        }
        return personName(users.get(id), id);
    }

    private String personName(UserDao user, int id) {
        if (user == null) {
            return "#" + id;
        }
        String name = ((user.getFirstName() == null ? "" : user.getFirstName()) + " "
                + (user.getLastName() == null ? "" : user.getLastName())).trim();
        return name.isBlank() ? "#" + id : name;
    }

    private long parseQty(String raw) {
        if (raw == null || raw.isBlank()) {
            return 0;
        }
        try {
            return Math.round(Double.parseDouble(raw.trim()));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private double parseMoney(String raw) {
        if (raw == null || raw.isBlank()) {
            return 0;
        }
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static class ProductAgg {
        long qty;
        double amount;
        Map<Integer, Long> byStore = new HashMap<>();
    }

    private static class StoreAgg {
        int orders;
        long units;
    }

    private static class PairAgg {
        final int storeId;
        final String code;
        long qty;
        int orders;

        PairAgg(int storeId, String code) {
            this.storeId = storeId;
            this.code = code;
        }
    }
}
