package boot.jpa.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job HeroJob() {
        return jobBuilderFactory.get("HeroJob")
                .start(HeroFirstStep(null))
                .next(HeroSecondStep(null))
                .build();
    }

    /**jobParameters setting
     * 1. shift + shift
     * 2. Search Edit Configurations
     * 3. BatchApplication
     * 4. Environment
     * 5. Program arguments : requestDate=20191227
     * */
    @Bean
    @JobScope
    public Step HeroFirstStep(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("HeroFirstStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("|-->[Configuration] JobConfiguration.HeroFirstStep(" + requestDate + ")");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    @JobScope
    public Step HeroSecondStep(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("HeroSecondStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("|-->[Configuration] JobConfiguration.HeroSecondStep(" + requestDate + ")");
                    return RepeatStatus.FINISHED;
                }).build();
    }
}