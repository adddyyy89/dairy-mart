package com.dairymart.dairyappexceldump;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SalesmanWriter implements ItemWriter<YourEntity>, ItemStream {

    private Workbook workbook;
    private Sheet sheet;
    private int rowNum = 0;
    private File outputFile;
    private FileOutputStream fos;

    public ExcelItemWriter(File outputFile) {
        this.outputFile = outputFile;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            // Check if file exists and open it, otherwise create new workbook
            if (outputFile.exists()) {
                // If appending to an existing file, load it. For fresh dump, delete first.
                // For a daily dump, we typically want a fresh file each day.
                // So, ensure the file is created or overwritten.
                System.out.println("Creating new Excel file: " + outputFile.getAbsolutePath());
                workbook = new XSSFWorkbook();
            } else {
                workbook = new XSSFWorkbook();
            }

            sheet = workbook.createSheet("Data Export");

            // Create header row
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Value");
            headerRow.createCell(3).setCellValue("Created At");

            fos = new FileOutputStream(outputFile);
        } catch (IOException e) {
            throw new ItemStreamException("Error opening Excel file for writing", e);
        }
    }

    @Override
    public void write(Chunk<? extends YourEntity> chunk) throws Exception {
        for (YourEntity item : chunk.getItems()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getId());
            row.createCell(1).setCellValue(item.getName());
            row.createCell(2).setCellValue(item.getValue());
            row.createCell(3).setCellValue(item.getCreatedAt().toString()); // Convert LocalDate to String
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // Not strictly necessary for this simple writer, but good practice
        // Can store state like rowNum if you were doing more complex restarts
    }

    @Override
    public void close() throws ItemStreamException {
        try {
            if (workbook != null && fos != null) {
                workbook.write(fos);
                fos.close();
                workbook.close();
                System.out.println("Excel file written successfully to: " + outputFile.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new ItemStreamException("Error closing Excel file after writing", e);
        }
    }

    // This method is useful to get the generated file path for the Google Drive uploader
    public String getOutputFilePath() {
        return outputFile.getAbsolutePath();
    }
}
