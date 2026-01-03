package com.dairymart.dairyappexceldump;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@SpringBootApplication
@EnableBatchProcessing // Enables Spring Batch features
@EnableScheduling
public class DairyappexceldumpApplication {

	private final JobLauncher jobLauncher;
	private final Job dailyDataExportJob;

	public DairyappexceldumpApplication(JobLauncher jobLauncher, Job dailyDataExportJob) {
		this.jobLauncher = jobLauncher;
		this.dailyDataExportJob = dailyDataExportJob;
	}

	public static void main(String[] args) {
		SpringApplication.run(DairyappexceldumpApplication.class, args);
	}

	/**
	 * Schedules the batch job to run daily at a specific time.
	 * Example: Runs every day at 11:59 PM (23:59).
	 * You can adjust the cron expression as needed.
	 */
	@Scheduled(cron = "0 59 23 * * ?") // Seconds Minutes Hours DayOfMonth Month DayOfWeek
	public void runDailyDataExportJob() {
		System.out.println("Attempting to launch dailyDataExportJob...");
		try {
			JobParameters jobParameters = new JobParametersBuilder()
					.addDate("runTime", new Date()) // Unique parameter for each job instance
					.toJobParameters();
			jobLauncher.run(dailyDataExportJob, jobParameters);
			System.out.println("dailyDataExportJob launched successfully.");
		} catch (JobExecutionAlreadyRunningException | JobRestartException |
				 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			System.err.println("Error launching dailyDataExportJob: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
