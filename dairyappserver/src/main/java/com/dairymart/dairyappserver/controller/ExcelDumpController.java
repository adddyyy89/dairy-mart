package com.dairymart.dairyappserver.controller;

import com.dairymart.dairyappserver.service.ExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/exceldump")
public class ExcelDumpController {

    Logger logger = LoggerFactory.getLogger(ExcelDumpController.class);

    private static final String FILE_NAME = "DairyMartDump.xlsx";

    @Autowired
    private ExcelService excelService;

    @GetMapping(path = "/generate")
    public String test() {
        logger.info("Excel generate dump called.");
        excelService.updateExcel();

        return "dump genereted";
    }

    @GetMapping("/download") // Endpoint to trigger Excel file download
    public ResponseEntity<Resource> downloadExcel() {
        try {
            // Determine the file path for reading. This assumes the file is in the application's working directory.
            Path directoryPath = Paths.get(System.getProperty("user.dir"));
            Path filePath = directoryPath.resolve(FILE_NAME);
            File file = filePath.toFile();

            // Check if the file exists
            if (!file.exists() || !file.canRead()) {
                // Return a 404 Not Found or 500 Internal Server Error if the file doesn't exist or isn't readable
                return ResponseEntity.notFound().build();
            }

            // Create an InputStreamResource from the file
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            // Set the HTTP headers for file download
            HttpHeaders headers = new HttpHeaders();
            // Set Content-Disposition to 'attachment' to prompt download
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
            // Set Content-Type to application/vnd.openxmlformats-officedocument.spreadsheetml.sheet for .xlsx files
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").toString());
            // Set Content-Length based on the file size
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));

            // Return the ResponseEntity with the resource, headers, and OK status
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);

        } catch (IOException e) {
            System.err.println("Error downloading Excel file: " + e.getMessage());
            logger.error("Error downloading Excel file: " + e.getMessage());
            // Return a 500 Internal Server Error in case of an I/O exception
            return ResponseEntity.internalServerError().build();
        }
    }

}
