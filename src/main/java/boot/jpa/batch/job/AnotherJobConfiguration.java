package boot.jpa.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AnotherJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    /**How to run this job only
     * 1. shift + shift
     * 2. Search Edit Configurations
     * 3. BatchApplication
     * 4. Environment
     * 5. Program arguments : --job.name=AnotherHeroJob version=2
     * 6. application.yml : spring:batch:job:names: ${job.name:NONE}
     * */
    @Bean
    public Job AnotherHeroJob() {
        return jobBuilderFactory.get("AnotherHeroJob")
                .start(AnotherHeroFirstStep())
                .next(AnotherHeroSecondStep())
                .next(AnotherHeroThirdStep())
                .build();
    }

    @Bean
    public Step AnotherHeroFirstStep() {
        return stepBuilderFactory.get("AnotherHeroFirstStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("|-->[Configuration] AnotherJobConfiguration.AnotherHeroFirstStep()");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step AnotherHeroSecondStep() {
        return stepBuilderFactory.get("AnotherHeroSecondStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("|-->[Configuration] AnotherJobConfiguration.AnotherHeroSecondStep()");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step AnotherHeroThirdStep() {
        return stepBuilderFactory.get("AnotherHeroThirdStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("|-->[Configuration] AnotherJobConfiguration.AnotherHeroThirdStep()");
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
