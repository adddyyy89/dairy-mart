package com.dairymart.dairyappexceldump;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final GoogleDriveUploadTasklet googleDriveUploadTasklet;

    @Value("${google.drive.output.file.path:./exported_data.xlsx}") // Default path for temp file
    private String excelFilePath;

    public BatchConfig(JobRepository jobRepository,
                       PlatformTransactionManager transactionManager,
                       EntityManagerFactory entityManagerFactory,
                       GoogleDriveUploadTasklet googleDriveUploadTasklet) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.entityManagerFactory = entityManagerFactory;
        this.googleDriveUploadTasklet = googleDriveUploadTasklet;
    }

    // 1. ItemReader: Reads data from PostgreSQL
    @Bean
    public ItemReader<YourEntity> yourEntityItemReader() {
        return new JpaPagingItemReaderBuilder<YourEntity>()
                .name("yourEntityItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT e FROM YourEntity e") // Query to fetch all data
                .pageSize(100) // Read in chunks of 100
                .build();
    }

    // 2. ItemWriter: Writes data to an Excel file
    @Bean
    public ExcelItemWriter excelItemWriter() {
        // Generate a dynamic file name based on the current date
        String fileName = "DailyDataExport_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        File outputFile = new File(fileName);
        this.excelFilePath = outputFile.getAbsolutePath(); // Update the path for the tasklet
        return new ExcelItemWriter(outputFile);
    }

    // 3. Step: Combines reader, writer, and defines chunk size
    @Bean
    public Step exportToExcelStep() {
        return new StepBuilder("exportToExcelStep", jobRepository)
                .<YourEntity, YourEntity>chunk(100, transactionManager) // Process in chunks of 100
                .reader(yourEntityItemReader())
                // .processor(yourEntityItemProcessor()) // Optional: Add a processor if you need data transformation
                .writer(excelItemWriter())
                .build();
    }

    // 4. Tasklet for Google Drive Upload
    // This tasklet will be executed after the Excel file is generated.
    @Bean
    public Step uploadToGoogleDriveStep() {
        return new StepBuilder("uploadToGoogleDriveStep", jobRepository)
                .tasklet(googleDriveUploadTasklet, transactionManager)
                .build();
    }

    // 5. Job: Orchestrates the steps
    @Bean
    public Job dailyDataExportJob() {
        return new JobBuilder("dailyDataExportJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // Ensures a unique job instance each run
                .start(exportToExcelStep()) // First, export to Excel
                .next(uploadToGoogleDriveStep()) // Then, upload to Google Drive
                .build();
    }
}
