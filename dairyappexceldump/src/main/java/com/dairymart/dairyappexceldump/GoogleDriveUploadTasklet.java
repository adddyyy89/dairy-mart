package com.dairymart.dairyappexceldump;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * Uploads the dump workbook to Drive when credentials and a folder id are configured.
 * Skips quietly if those are missing so a local dump still succeeds.
 */
@Component
public class GoogleDriveUploadTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveUploadTasklet.class);
    private static final String APPLICATION_NAME = "Dairy Mart Excel Dump";

    @Value("${google.drive.credentials.path:}")
    private String credentialsPath;

    @Value("${google.drive.folder.id:}")
    private String folderId;

    private final ResourceLoader resourceLoader;

    public GoogleDriveUploadTasklet(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String excelFilePath = chunkContext.getStepContext().getStepExecution().getJobExecution()
                .getExecutionContext().getString(BatchConfig.EXCEL_FILE_KEY, "");
        if (excelFilePath.isBlank() || folderId == null || folderId.isBlank()
                || "your_google_drive_folder_id".equals(folderId) || credentialsPath == null || credentialsPath.isBlank()) {
            logger.info("Skipping Google Drive upload (no folder id or credentials).");
            return RepeatStatus.FINISHED;
        }
        Resource resource = resourceLoader.getResource(credentialsPath);
        if (!resource.exists()) {
            logger.info("Skipping Google Drive upload (credentials file not found).");
            return RepeatStatus.FINISHED;
        }
        File uploadFile = new File(excelFilePath);
        if (!uploadFile.exists()) {
            throw new IllegalStateException("Excel dump file was not found: " + excelFilePath);
        }
        try (InputStream in = resource.getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                    .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));
            Drive service = new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName("DairyMartDump_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx");
            fileMetadata.setParents(Collections.singletonList(folderId));
            FileContent mediaContent = new FileContent(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", uploadFile);
            service.files().create(fileMetadata, mediaContent).setFields("id").execute();
            logger.info("Uploaded dump to Google Drive.");
        }
        return RepeatStatus.FINISHED;
    }
}
