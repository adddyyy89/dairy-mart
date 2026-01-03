package com.dairymart.dairyappexceldump;

import com.google.api.services.drive.Drive;
import org.apache.xmlbeans.ResourceLoader;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

@Component
public class GoogleDriveUploadTasklet implements Tasklet {

    private static final String APPLICATION_NAME = "Daily Data Exporter";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Value("${google.drive.credentials.path}")
    private String credentialsPath; // Path to service-account-key.json
    @Value("${google.drive.folder.id}")
    private String folderId;
    @Value("${google.drive.output.file.path:./exported_data.xlsx}") // Default path
    private String excelFilePath;

    private final ResourceLoader resourceLoader;

    public GoogleDriveUploadTasklet(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("Starting Google Drive upload for file: " + excelFilePath);
        try {
            uploadFileToGoogleDrive();
            System.out.println("File uploaded to Google Drive successfully.");
            // Optionally, delete the local file after successful upload
            File localFile = new File(excelFilePath);
            if (localFile.exists()) {
                if (localFile.delete()) {
                    System.out.println("Local Excel file deleted: " + excelFilePath);
                } else {
                    System.err.println("Failed to delete local Excel file: " + excelFilePath);
                }
            }
        } catch (Exception e) {
            System.err.println("Error uploading file to Google Drive: " + e.getMessage());
            throw e; // Propagate exception to fail the batch step
        }
        return RepeatStatus.FINISHED;
    }

    private Drive getDriveService() throws IOException, GeneralSecurityException {
        // Load credentials from the service account key file
        Resource resource = resourceLoader.getResource(credentialsPath);
        if (!resource.exists()) {
            throw new IOException("Service account key file not found: " + credentialsPath);
        }

        InputStream in = resource.getInputStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));

        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private void uploadFileToGoogleDrive() throws IOException, GeneralSecurityException {
        Drive service = getDriveService();

        File uploadFile = new File(excelFilePath);
        if (!uploadFile.exists()) {
            throw new IOException("Excel file not found for upload: " + excelFilePath);
        }

        // Generate a dynamic name for the Google Drive file
        String googleDriveFileName = "DailyDataExport_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";

        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(googleDriveFileName);
        fileMetadata.setParents(Collections.singletonList(folderId)); // Set the parent folder

        FileContent mediaContent = new FileContent("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", uploadFile);

        com.google.api.services.drive.model.File uploadedFile = service.files().create(fileMetadata, mediaContent)
                .setFields("id,webContentLink,webViewLink")
                .execute();

        System.out.println("File ID: " + uploadedFile.getId());
        System.out.println("Web Content Link: " + uploadedFile.getWebContentLink()); // Direct download link
        System.out.println("Web View Link: " + uploadedFile.getWebViewLink());       // View in browser link
    }
}
