package boot.jpa.batch.job;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

    @Bean
    public JobLauncherTestUtils JobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }
}
