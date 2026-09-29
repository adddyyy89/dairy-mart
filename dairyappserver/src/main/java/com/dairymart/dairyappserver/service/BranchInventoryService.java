package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.BranchInventoryDao;
import com.dairymart.dairyappserver.dao.BranchInventoryId;
import com.dairymart.dairyappserver.dao.ProductDao;
import com.dairymart.dairyappserver.dao.RetailOrderDao;
import com.dairymart.dairyappserver.dao.RetailOrderDetailsDao;
import com.dairymart.dairyappserver.dto.InventoryItemDTO;
import com.dairymart.dairyappserver.repository.BranchInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class BranchInventoryService {

    @Autowired
    private BranchInventoryRepository inventoryRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private BranchService branchService;

    public List<InventoryItemDTO> listForBranch(int branchId) {
        List<InventoryItemDTO> rows = new ArrayList<>();
        for (ProductDao product : productService.getAllProducts()) {
            if (Boolean.FALSE.equals(product.getActive())) {
                continue;
            }
            InventoryItemDTO dto = toProductRow(branchId, product);
            dto.setQuantity(getQuantity(branchId, product.getProductId()));
            dto.setAvailable(dto.getQuantity());
            rows.add(dto);
        }
        return rows;
    }

    public InventoryItemDTO setQuantity(int branchId, int productId, int quantity) {
        if (branchService.findById(branchId) == null) {
            throw new IllegalArgumentException("Invalid branch.");
        }
        ProductDao product = productService.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Invalid product.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        BranchInventoryDao row = getOrCreate(branchId, productId);
        row.setQuantity(quantity);
        row.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        inventoryRepository.save(row);
        InventoryItemDTO dto = toProductRow(branchId, product);
        dto.setQuantity(quantity);
        dto.setAvailable(quantity);
        return dto;
    }

    public List<InventoryItemDTO> availabilityForOrder(RetailOrderDao order) {
        List<InventoryItemDTO> rows = new ArrayList<>();
        if (order == null || order.getOrderDetails() == null) {
            return rows;
        }
        int branchId = order.getBranchId() > 0 ? order.getBranchId() : 7;
        for (RetailOrderDetailsDao line : order.getOrderDetails()) {
            ProductDao product = findProduct(line.getProductCode());
            int ordered = parseQty(line.getQuantity());
            int available = product == null ? 0 : getQuantity(branchId, product.getProductId());
            InventoryItemDTO dto = new InventoryItemDTO();
            dto.setBranchId(branchId);
            dto.setProductId(product == null ? 0 : product.getProductId());
            dto.setProductCode(line.getProductCode());
            dto.setProductName(product == null ? line.getProductCode() : product.getProductName());
            dto.setOrdered(ordered);
            dto.setAvailable(available);
            dto.setQuantity(available);
            dto.setShortfall(Math.max(0, ordered - available));
            rows.add(dto);
        }
        return rows;
    }

    public void applyStatusChange(RetailOrderDao order, int previousStatusId, int newStatusId) {
        if (order == null || previousStatusId == newStatusId) {
            return;
        }
        boolean wasConsuming = consumesStock(previousStatusId);
        boolean willConsume = consumesStock(newStatusId);
        if (!wasConsuming && willConsume) {
            List<InventoryItemDTO> check = availabilityForOrder(order);
            StringBuilder shortfall = new StringBuilder();
            for (InventoryItemDTO row : check) {
                if (row.getShortfall() > 0) {
                    if (shortfall.length() > 0) {
                        shortfall.append("; ");
                    }
                    shortfall.append(row.getProductName())
                            .append(" needs ")
                            .append(row.getOrdered())
                            .append(", branch has ")
                            .append(row.getAvailable());
                }
            }
            if (shortfall.length() > 0) {
                throw new IllegalArgumentException("Not enough branch stock: " + shortfall);
            }
            adjust(order, -1);
        } else if (wasConsuming && !willConsume) {
            adjust(order, 1);
        }
    }

    private void adjust(RetailOrderDao order, int sign) {
        int branchId = order.getBranchId() > 0 ? order.getBranchId() : 7;
        if (order.getOrderDetails() == null) {
            return;
        }
        for (RetailOrderDetailsDao line : order.getOrderDetails()) {
            ProductDao product = findProduct(line.getProductCode());
            if (product == null) {
                continue;
            }
            int qty = parseQty(line.getQuantity());
            BranchInventoryDao row = getOrCreate(branchId, product.getProductId());
            row.setQuantity(Math.max(0, row.getQuantity() + (sign * qty)));
            row.setLastUpdated(new Timestamp(System.currentTimeMillis()));
            inventoryRepository.save(row);
        }
    }

    private boolean consumesStock(int statusId) {
        return statusId == 2 || statusId == 4 || statusId == 5;
    }

    private BranchInventoryDao getOrCreate(int branchId, int productId) {
        return inventoryRepository.findById(new BranchInventoryId(branchId, productId)).orElseGet(() -> {
            BranchInventoryDao row = new BranchInventoryDao();
            row.setBranchId(branchId);
            row.setProductId(productId);
            row.setQuantity(0);
            return row;
        });
    }

    private int getQuantity(int branchId, int productId) {
        return inventoryRepository.findById(new BranchInventoryId(branchId, productId))
                .map(BranchInventoryDao::getQuantity)
                .orElse(0);
    }

    private InventoryItemDTO toProductRow(int branchId, ProductDao product) {
        InventoryItemDTO dto = new InventoryItemDTO();
        dto.setBranchId(branchId);
        dto.setProductId(product.getProductId());
        dto.setProductCode(product.getProductCode());
        dto.setProductName(product.getProductName());
        return dto;
    }

    private ProductDao findProduct(String productCode) {
        if (productCode == null || productCode.isBlank()) {
            return null;
        }
        String code = productCode.trim();
        return productService.getAllProducts().stream()
                .filter(p -> p.getProductCode() != null && p.getProductCode().trim().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

    private int parseQty(String raw) {
        if (raw == null || raw.isBlank()) {
            return 0;
        }
        try {
            return (int) Math.round(Double.parseDouble(raw.trim()));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
