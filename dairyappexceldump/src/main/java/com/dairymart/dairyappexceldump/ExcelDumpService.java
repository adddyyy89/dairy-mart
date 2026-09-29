package com.dairymart.dairyappexceldump;

import com.dairymart.dairyappexceldump.dao.AppNotificationDao;
import com.dairymart.dairyappexceldump.dao.BranchInventoryDao;
import com.dairymart.dairyappexceldump.dao.CrateDao;
import com.dairymart.dairyappexceldump.dao.CratePoolDao;
import com.dairymart.dairyappexceldump.dao.LedgerDao;
import com.dairymart.dairyappexceldump.dao.LedgerTransactionsDao;
import com.dairymart.dairyappexceldump.dao.ProductDao;
import com.dairymart.dairyappexceldump.dao.RetailOrderDao;
import com.dairymart.dairyappexceldump.dao.RetailOrderDetailsDao;
import com.dairymart.dairyappexceldump.dao.ScheduledNotificationDao;
import com.dairymart.dairyappexceldump.dao.ShopDao;
import com.dairymart.dairyappexceldump.dao.TrackingDao;
import com.dairymart.dairyappexceldump.dao.UserDao;
import com.dairymart.dairyappexceldump.dao.UserLoginDao;
import com.dairymart.dairyappexceldump.dao.UserWalletDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Writes a point-in-time Excel dump of live Dairy Mart tables.
 * Passwords are never written. Missing optional tables are skipped.
 */
@Service
public class ExcelDumpService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelDumpService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${dump.output.dir:}")
    private String outputDir;

    /**
     * Builds DairyMartDump.xlsx (backing up any previous file) and returns it.
     */
    public File writeDump() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        writeUsers(workbook);
        writeShops(workbook);
        writeProducts(workbook);
        writeOrders(workbook);
        writeOrderDetails(workbook);
        writeInventory(workbook);
        writeCrates(workbook);
        writeCratePool(workbook);
        writeLedgers(workbook);
        writeTransactions(workbook);
        writeWallets(workbook);
        writeNotifications(workbook);
        writeScheduled(workbook);
        writeTracking(workbook);
        writeLogins(workbook);

        Path dir = (outputDir == null || outputDir.isBlank())
                ? Path.of(System.getProperty("user.dir"))
                : Path.of(outputDir);
        File target = dir.resolve("DairyMartDump.xlsx").toFile();
        if (target.exists()) {
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File backup = dir.resolve("DairyMartDump_" + stamp + ".xlsx").toFile();
            if (!target.renameTo(backup)) {
                logger.warn("Could not rename previous dump {}", target.getAbsolutePath());
            }
        }
        try (FileOutputStream out = new FileOutputStream(target)) {
            workbook.write(out);
        }
        workbook.close();
        logger.info("Wrote dump {}", target.getAbsolutePath());
        return target;
    }

    private void writeUsers(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Users");
        header(sheet, "UserId", "Name", "Phone", "TypeId", "Email", "Active", "CrateCount");
        int row = 1;
        for (UserDao user : list(UserDao.class, "SELECT u FROM UserDao u")) {
            Row r = sheet.createRow(row++);
            int c = 0;
            cell(r, c++, user.getUserId());
            cell(r, c++, name(user));
            cell(r, c++, user.getPhoneNumber());
            cell(r, c++, user.getTypeId());
            cell(r, c++, user.getEmailId());
            cell(r, c++, String.valueOf(user.getActive()));
            cell(r, c++, user.getCrateCount());
        }
        autosize(sheet, 7);
    }

    private void writeShops(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Shops");
        header(sheet, "ShopId", "ShopName", "UserId", "Active");
        int row = 1;
        for (ShopDao shop : list(ShopDao.class, "SELECT s FROM ShopDao s")) {
            Row r = sheet.createRow(row++);
            cell(r, 0, shop.getShopId());
            cell(r, 1, shop.getShopName());
            cell(r, 2, shop.getUserId());
            cell(r, 3, String.valueOf(shop.isActive()));
        }
        autosize(sheet, 4);
    }

    private void writeProducts(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Products");
        header(sheet, "ProductId", "Code", "Name", "Active");
        int row = 1;
        for (ProductDao product : list(ProductDao.class, "SELECT p FROM ProductDao p")) {
            Row r = sheet.createRow(row++);
            cell(r, 0, product.getProductId());
            cell(r, 1, product.getProductCode());
            cell(r, 2, product.getProductName());
            cell(r, 3, String.valueOf(product.getActive()));
        }
        autosize(sheet, 4);
    }

    private void writeOrders(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Orders");
        header(sheet, "OrderId", "OrderDate", "Shop", "BranchId", "StatusId", "Status", "CreatedBy");
        int row = 1;
        for (RetailOrderDao order : list(RetailOrderDao.class, "SELECT o FROM RetailOrderDao o")) {
            Row r = sheet.createRow(row++);
            int c = 0;
            cell(r, c++, order.getOrderId());
            cell(r, c++, order.getOrderDate() == null ? "" : order.getOrderDate().toString());
            cell(r, c++, order.getRetailer() == null ? String.valueOf(order.getRetailerId()) : order.getRetailer().getShopName());
            cell(r, c++, order.getBranchId());
            cell(r, c++, order.getOrderStatusId());
            cell(r, c++, order.getStatus() == null ? "" : order.getStatus().getStatusDesc());
            cell(r, c++, order.getCreatedBy());
        }
        autosize(sheet, 7);
    }

    private void writeOrderDetails(Workbook workbook) {
        Sheet sheet = workbook.createSheet("OrderDetails");
        header(sheet, "OrderId", "ProductCode", "Quantity", "Unit", "SaleRate");
        int row = 1;
        for (RetailOrderDetailsDao line : list(RetailOrderDetailsDao.class, "SELECT d FROM RetailOrderDetailsDao d")) {
            Row r = sheet.createRow(row++);
            cell(r, 0, line.getOrderId());
            cell(r, 1, line.getProductCode());
            cell(r, 2, line.getQuantity());
            cell(r, 3, line.getUnit());
            cell(r, 4, line.getSaleRate());
        }
        autosize(sheet, 5);
    }

    private void writeInventory(Workbook workbook) {
        Sheet sheet = workbook.createSheet("BranchInventory");
        header(sheet, "BranchId", "ProductId", "Quantity", "LastUpdated");
        int row = 1;
        for (BranchInventoryDao item : list(BranchInventoryDao.class, "SELECT i FROM BranchInventoryDao i")) {
            Row r = sheet.createRow(row++);
            cell(r, 0, item.getBranchId());
            cell(r, 1, item.getProductId());
            cell(r, 2, item.getQuantity());
            cell(r, 3, item.getLastUpdated() == null ? "" : item.getLastUpdated().toString());
        }
        autosize(sheet, 4);
    }

    private void writeCrates(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Crates");
        header(sheet, "UserId", "UserTypeId", "Holding", "Received", "Returned", "When");
        int row = 1;
        for (CrateDao crate : list(CrateDao.class, "SELECT c FROM CrateDao c")) {
            Row r = sheet.createRow(row++);
            cell(r, 0, crate.getUserId());
            cell(r, 1, crate.getUserTypeId());
            cell(r, 2, crate.getCrateCount());
            cell(r, 3, crate.getCrateReceived());
            cell(r, 4, crate.getCrateReturned());
            cell(r, 5, crate.getRecordTimestamp() == null ? "" : crate.getRecordTimestamp().toString());
        }
        autosize(sheet, 6);
    }

    private void writeCratePool(Workbook workbook) {
        Sheet sheet = workbook.createSheet("CratePool");
        header(sheet, "PoolId", "Total", "AtBranch", "LastUpdated");
        int row = 1;
        for (CratePoolDao pool : list(CratePoolDao.class, "SELECT p FROM CratePoolDao p")) {
            Row r = sheet.createRow(row++);
            cell(r, 0, pool.getPoolId());
            cell(r, 1, pool.getTotalCrates());
            cell(r, 2, pool.getAvailableAtBranch());
            cell(r, 3, pool.getLastUpdated() == null ? "" : pool.getLastUpdated().toString());
        }
        autosize(sheet, 4);
    }

    private void writeLedgers(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Ledgers");
        header(sheet, "LedgerId", "SalesmanId", "RetailerId", "Active");
        int row = 1;
        for (LedgerDao ledger : list(LedgerDao.class, "SELECT l FROM LedgerDao l")) {
            Row r = sheet.createRow(row++);
            cell(r, 0, ledger.getLedgerId());
            cell(r, 1, ledger.getSalesmanId());
            cell(r, 2, ledger.getRetailerId());
            cell(r, 3, String.valueOf(ledger.isActive()));
        }
        autosize(sheet, 4);
    }

    private void writeTransactions(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Transactions");
        header(sheet, "TxnId", "LedgerId", "Amount", "Credit", "Debit", "When");
        int row = 1;
        for (LedgerTransactionsDao tx : list(LedgerTransactionsDao.class, "SELECT t FROM LedgerTransactionsDao t")) {
            Row r = sheet.createRow(row++);
            cell(r, 0, tx.getTransactionId());
            cell(r, 1, tx.getLedgerId());
            cell(r, 2, tx.getAmount());
            cell(r, 3, String.valueOf(tx.isCredit()));
            cell(r, 4, String.valueOf(tx.isDebit()));
            cell(r, 5, tx.getCreatedOn() == null ? "" : tx.getCreatedOn().toString());
        }
        autosize(sheet, 6);
    }

    private void writeWallets(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Wallets");
        header(sheet, "WalletId", "UserId", "Balance", "Outstanding");
        int row = 1;
        for (UserWalletDao wallet : list(UserWalletDao.class, "SELECT w FROM UserWalletDao w")) {
            Row r = sheet.createRow(row++);
            cell(r, 0, wallet.getWalletId());
            cell(r, 1, wallet.getUserId());
            cell(r, 2, wallet.getBalance());
            cell(r, 3, wallet.getOutstanding());
        }
        autosize(sheet, 4);
    }

    private void writeNotifications(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Notifications");
        header(sheet, "Id", "UserId", "Kind", "Title", "Message", "Read", "When");
        int row = 1;
        for (AppNotificationDao n : list(AppNotificationDao.class, "SELECT n FROM AppNotificationDao n")) {
            Row r = sheet.createRow(row++);
            cell(r, 0, n.getNotificationId());
            cell(r, 1, n.getUserId());
            cell(r, 2, n.getKind());
            cell(r, 3, n.getTitle());
            cell(r, 4, n.getMessage());
            cell(r, 5, String.valueOf(n.isRead()));
            cell(r, 6, n.getCreatedOn() == null ? "" : n.getCreatedOn().toString());
        }
        autosize(sheet, 7);
    }

    private void writeScheduled(Workbook workbook) {
        Sheet sheet = workbook.createSheet("ScheduledNotices");
        header(sheet, "Id", "Title", "Audience", "When", "Released", "Cancelled");
        int row = 1;
        for (ScheduledNotificationDao n : list(ScheduledNotificationDao.class, "SELECT n FROM ScheduledNotificationDao n")) {
            Row r = sheet.createRow(row++);
            cell(r, 0, n.getScheduleId());
            cell(r, 1, n.getTitle());
            cell(r, 2, n.getAudience());
            cell(r, 3, n.getScheduledFor() == null ? "" : n.getScheduledFor().toString());
            cell(r, 4, String.valueOf(n.isReleased()));
            cell(r, 5, String.valueOf(n.isCancelled()));
        }
        autosize(sheet, 6);
    }

    private void writeTracking(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Tracking");
        header(sheet, "TrackId", "UserId", "Latitude", "Longitude", "When");
        int row = 1;
        for (TrackingDao ping : list(TrackingDao.class, "SELECT t FROM TrackingDao t")) {
            Row r = sheet.createRow(row++);
            cell(r, 0, ping.getTrackId());
            cell(r, 1, ping.getUserId());
            cell(r, 2, ping.getLatitude() == null ? "" : String.valueOf(ping.getLatitude()));
            cell(r, 3, ping.getLongitude() == null ? "" : String.valueOf(ping.getLongitude()));
            cell(r, 4, ping.getTimestamp() == null ? "" : ping.getTimestamp().toString());
        }
        autosize(sheet, 5);
    }

    private void writeLogins(Workbook workbook) {
        Sheet sheet = workbook.createSheet("UserLogins");
        header(sheet, "UserId", "Phone", "Role", "LoggedIn", "LoggedOut", "Active");
        int row = 1;
        for (UserLoginDao login : list(UserLoginDao.class, "SELECT l FROM UserLoginDao l")) {
            Row r = sheet.createRow(row++);
            cell(r, 0, login.getUserId());
            cell(r, 1, login.getPhoneNumber());
            cell(r, 2, login.getRole());
            cell(r, 3, login.getLoggedIn() == null ? "" : login.getLoggedIn().toString());
            cell(r, 4, login.getLoggedOut() == null ? "" : login.getLoggedOut().toString());
            cell(r, 5, String.valueOf(login.isActive()));
        }
        autosize(sheet, 6);
    }

    private <T> List<T> list(Class<T> type, String jpql) {
        try {
            return entityManager.createQuery(jpql, type).getResultList();
        } catch (Exception ex) {
            logger.warn("Skipping {} ({})", type.getSimpleName(), ex.getMessage());
            return List.of();
        }
    }

    private void header(Sheet sheet, String... titles) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            row.createCell(i).setCellValue(titles[i]);
        }
    }

    private void cell(Row row, int col, Object value) {
        if (value == null) {
            row.createCell(col).setCellValue("");
        } else if (value instanceof Number number) {
            row.createCell(col).setCellValue(number.doubleValue());
        } else {
            row.createCell(col).setCellValue(String.valueOf(value));
        }
    }

    private void autosize(Sheet sheet, int cols) {
        for (int i = 0; i < cols; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private String name(UserDao user) {
        return ((user.getFirstName() == null ? "" : user.getFirstName()) + " "
                + (user.getLastName() == null ? "" : user.getLastName())).trim();
    }
}
