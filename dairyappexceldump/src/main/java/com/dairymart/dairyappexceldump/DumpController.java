package com.dairymart.dairyappexceldump;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/dump")
public class DumpController {

    private final JobLauncher jobLauncher;
    private final Job dailyDataExportJob;

    public DumpController(JobLauncher jobLauncher, Job dailyDataExportJob) {
        this.jobLauncher = jobLauncher;
        this.dailyDataExportJob = dailyDataExportJob;
    }

    /** Runs the same nightly dump immediately. */
    @PostMapping("/run")
    public ResponseEntity<Map<String, String>> run() throws Exception {
        jobLauncher.run(dailyDataExportJob, new JobParametersBuilder()
                .addDate("runTime", new Date())
                .toJobParameters());
        return ResponseEntity.ok(Map.of("message", "Dump started."));
    }
}
