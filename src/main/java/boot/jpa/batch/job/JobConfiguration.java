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
@RequiredArgsConstructor
@Configuration
public class JobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job HeroJob() {
        return jobBuilderFactory.get("HeroJob")
                .start(HeroStep())
                .build();
    }

    @Bean
    public Step HeroStep() {
        return stepBuilderFactory.get("HeroStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("|-->[Configuration] JobConfiguration.HeroStep()");
                    return RepeatStatus.FINISHED;
                }).build();
    }
}