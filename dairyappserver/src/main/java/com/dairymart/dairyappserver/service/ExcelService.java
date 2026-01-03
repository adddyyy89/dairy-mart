package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelService {

    Logger logger = LoggerFactory.getLogger(ExcelService.class);

    private static final String BASE_FILE_NAME = "DairyMartDump";
    private static final String FILE_EXTENSION = ".xlsx";

    private Workbook workbook = new XSSFWorkbook();

    private int rowNum = 0;
    private File outputFile;
    private FileOutputStream fos;


    @Autowired
    private UserService userService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private RetailOrderService retailOrderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private LedgerService ledgerService;

    public void updateExcel() {

        // Dump Salesman data
        dumpSalesmanData();

        // Dump Retailer data
        dumpRetailerData();

        // Dump Retailer Orders
        dumpRetailOrders();

        // Dump Transactions
        dumpTransactions();

        // Will backup the existing dump
        saveFile();
    }

    private void dumpTransactions() {
        Sheet orderSheet = workbook.createSheet("Transactions");

        String[] headers = new String[8];
        headers[0] = "Ledger Id";
        headers[1] = "Transaction Id";
        headers[2] = "Salesman Name";
        headers[3] = "Retailer Name";
        headers[4] = "Credit Amount";
        headers[5] = "Debit Amount";
        headers[6] = "Date";
        headers[7] = "Payment Type";

        Row headerRow = orderSheet.createRow(0);

        // Create Header
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        List<LedgerTransactionsDao> ledgerTransactionsDaos = ledgerService.getAllLedgerTransactions();
        ledgerTransactionsDaos.sort(Comparator.comparing(LedgerTransactionsDao::getCreatedOn));


        int rowNum = 1;
        for(LedgerTransactionsDao ledgerTransactionsDao : ledgerTransactionsDaos) {
            Row row = orderSheet.createRow(rowNum++);

            int colNum = 0;

            row.createCell(colNum++).setCellValue(ledgerTransactionsDao.getLedgerId());
            row.createCell(colNum++).setCellValue(ledgerTransactionsDao.getTransactionId());
            row.createCell(colNum++).setCellValue(ledgerTransactionsDao.getLedger().getSalesman().getFirstName() + " " + ledgerTransactionsDao.getLedger().getSalesman().getLastName());
            row.createCell(colNum++).setCellValue(ledgerTransactionsDao.getLedger().getRetailer().getFirstName() + " " + ledgerTransactionsDao.getLedger().getRetailer().getLastName());

            if(ledgerTransactionsDao.isCredit()) {
                row.createCell(colNum++).setCellValue(ledgerTransactionsDao.getAmount());
            }
            else {
                row.createCell(colNum++).setCellValue("0");
            }
            if(ledgerTransactionsDao.isDebit()) {
                row.createCell(colNum++).setCellValue(ledgerTransactionsDao.getAmount());
            }
            else {
                row.createCell(colNum++).setCellValue("0");
            }

            row.createCell(colNum++).setCellValue(ledgerTransactionsDao.getCreatedOn().toLocalDateTime().toString());
            row.createCell(colNum++).setCellValue(ledgerTransactionsDao.getPaymentType().getProductTypeName());

        }

        // Auto-size columns for better readability
        for (int i = 0; i < headers.length; i++) {
            orderSheet.autoSizeColumn(i);
        }
    }

    private void dumpRetailOrders() {
        Sheet orderSheet = workbook.createSheet("Orders");

        List<ProductDao> productDaoList = productService.getAllProducts();
        productDaoList.sort(Comparator.comparing(ProductDao::getProductCode));

        String[] headers = new String[productDaoList.size()+5];
        headers[0] = "Order Date";
        headers[1] = "Shop Name";
        headers[2] = "Order Id";
        headers[3] = "Salesman Name";
        headers[4] = "Order Status";

        int productIndx = 0;
        for(int indx=5;indx<productDaoList.size()+5;indx++){
            headers[indx] = productDaoList.get(productIndx++).getProductName();
        }

        Map<String, String> productCodeMap = new HashMap<>();
        for(ProductDao productDao : productDaoList) {
            productCodeMap.put(productDao.getProductCode(), productDao.getProductName());
        }

        Row headerRow = orderSheet.createRow(0);

        // Create Header
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        List<RetailOrderDao> retailOrderDaos = retailOrderService.getAllOrders();

        int rowNum = 1;
        for(RetailOrderDao retailOrderDao : retailOrderDaos) {
            Row row = orderSheet.createRow(rowNum++);




            int colNum = 0;
            row.createCell(colNum++).setCellValue(retailOrderDao.getOrderDate().toLocalDate().toString());
            row.createCell(colNum++).setCellValue(retailOrderDao.getRetailer().getShopName());
            row.createCell(colNum++).setCellValue(retailOrderDao.getOrderId());
            // Get Salesman Name (linked to the retailer)
            UserDao salesmanDao = retailOrderService.getSalesmanUsingOrderId(retailOrderDao.getOrderId());
            row.createCell(colNum++).setCellValue(salesmanDao.getFirstName() + " " + salesmanDao.getLastName());

            row.createCell(colNum++).setCellValue(retailOrderDao.getStatus().getStatusDesc());


            for(RetailOrderDetailsDao retailOrderDetailsDao : retailOrderDao.getOrderDetails()) {

                String productCode = retailOrderDetailsDao.getProductCode();
                for(int idx = 5; idx < productDaoList.size()+5; idx++) {
                    if(headers[idx].equalsIgnoreCase(productCodeMap.get(productCode))) {
                        row.createCell(idx).setCellValue(retailOrderDetailsDao.getQuantity());
                        break;
                    }
                }
            }
        }

        // Auto-size columns for better readability
        for (int i = 0; i < headers.length; i++) {
            orderSheet.autoSizeColumn(i);
        }

    }

    private void dumpRetailerData() {
        Sheet retailerSheet = workbook.createSheet("Retailer");
        String[] headers = {"Retailer Name", "Shop Name", "Phone Number", "Address"};
        Row headerRow = retailerSheet.createRow(0);

        // Create Header
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Get Retailer data
        List<UserDao> usersList = userService.getAllUsers();
        List<UserDao> retailerList = usersList.stream().filter(user -> user.getTypeId() == 3).collect(Collectors.toCollection(ArrayList::new));
        List<ShopDao> shops = shopService.getAllShops();

        int rowNum = 1;
        for(UserDao retailer : retailerList) {
            Row row = retailerSheet.createRow(rowNum++);


            String shopName = "";
            String shopAddress = "";
            for(ShopDao shop : shops) {
                if(shop.getUserId() == retailer.getUserId()) {
                    shopName = shop.getShopName();
                    shopAddress = shop.getAddress().getFullAddress();
                    break;
                }
            }

            int colNum = 0;
            row.createCell(colNum++).setCellValue(retailer.getFirstName() + " " + retailer.getLastName());
            row.createCell(colNum++).setCellValue(shopName);
            row.createCell(colNum++).setCellValue(retailer.getPhoneNumber());
            row.createCell(colNum++).setCellValue(shopAddress);
        }

        // Auto-size columns for better readability
        for (int i = 0; i < headers.length; i++) {
            retailerSheet.autoSizeColumn(i);
        }

        // Save data
    }

    private void dumpSalesmanData() {
        Sheet salesmanSheet = workbook.createSheet("Salesman");
        String[] headers = {"Salesman Name", "Phone Number", "Address"};
        Row headerRow = salesmanSheet.createRow(0);

        // Create Header
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Get Salesman data
        List<UserDao> usersList = userService.getAllUsers();
        List<UserDao> salesmanList = usersList.stream().filter(user -> user.getTypeId() == 2).collect(Collectors.toCollection(ArrayList::new));


        int rowNum = 1;
        for(UserDao salesman : salesmanList) {
            Row row = salesmanSheet.createRow(rowNum++);




            int colNum = 0;
            row.createCell(colNum++).setCellValue(salesman.getFirstName() + " " + salesman.getLastName());
            row.createCell(colNum++).setCellValue(salesman.getPhoneNumber());
            row.createCell(colNum++).setCellValue(salesman.getAddress().getFullAddress());
        }

        // Auto-size columns for better readability
        for (int i = 0; i < headers.length; i++) {
            salesmanSheet.autoSizeColumn(i);
        }

    }

    private void saveFile(){
        // Determine the file path for writing
        Path directoryPath = Paths.get(System.getProperty("user.dir")); // Directory where the application is run
        String finalFileName = BASE_FILE_NAME + FILE_EXTENSION;
        Path filePath = directoryPath.resolve(finalFileName); // Initial file path

        // Check if the file already exists
        File targetFile = filePath.toFile();
        if (targetFile.exists()) {
            // If it exists, rename the existing file by appending a timestamp
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(formatter);
            String newOldFileName = BASE_FILE_NAME + timestamp + FILE_EXTENSION;
            Path newOldFilePath = directoryPath.resolve(newOldFileName);

            if (targetFile.renameTo(newOldFilePath.toFile())) {
                System.out.println("Existing file '" + finalFileName + "' renamed to '" + newOldFileName + "'");
            } else {
                System.err.println("Failed to rename existing file '" + finalFileName + "'.");
                // Continue, but the old file might not be moved
            }
        }

        // Write the new workbook to the file system (this will create a new file with the original name)
        try (FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {
            workbook.write(outputStream); // Write workbook content to file
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Excel file '" + BASE_FILE_NAME + "' written successfully to: " + filePath.toAbsolutePath());
    }
}
