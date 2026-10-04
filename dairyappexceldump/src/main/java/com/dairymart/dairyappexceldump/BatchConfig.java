package com.dairymart.dairyappexceldump;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

@Configuration
public class BatchConfig {

    public static final String EXCEL_FILE_KEY = "excelFilePath";

    @Bean
    public Step exportToExcelStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  ExcelDumpService excelDumpService) {
        return new StepBuilder("exportToExcelStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    File file = excelDumpService.writeDump();
                    chunkContext.getStepContext().getStepExecution().getJobExecution()
                            .getExecutionContext().putString(EXCEL_FILE_KEY, file.getAbsolutePath());
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step uploadToGoogleDriveStep(JobRepository jobRepository,
                                        PlatformTransactionManager transactionManager,
                                        GoogleDriveUploadTasklet googleDriveUploadTasklet) {
        return new StepBuilder("uploadToGoogleDriveStep", jobRepository)
                .tasklet(googleDriveUploadTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job dailyDataExportJob(JobRepository jobRepository, Step exportToExcelStep, Step uploadToGoogleDriveStep) {
        return new JobBuilder("dailyDataExportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(exportToExcelStep)
                .next(uploadToGoogleDriveStep)
                .build();
    }
}
