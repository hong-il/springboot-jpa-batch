package boot.jpa.batch.job.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TaskletConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    /**How to run this job only
     * 1. shift + shift
     * 2. Search Edit Configurations
     * 3. BatchApplication
     * 4. Environment
     * 5. Program arguments : --job.name=TaskletStep requestDate=20191230
     * */
    @Bean
    public Job TaskletStep() {
        return jobBuilderFactory.get("TaskletStep")
                .start(TaskletFirstStep(null))
                .next(TaskletSecondStep())
                .build();
    }

    /**JobScope can use
     * 1. Step
     * JobParameters can use
     * 1. Double
     * 2. Long
     * 3. Date
     * 4. String
     * JobParameters are used in JobScope and StepScope
     * */
    @Bean
    @JobScope
    public Step TaskletFirstStep(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("TaskletFirstStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("|-->[Configuration] TaskletConfiguration.TaskletFirstStep(" + requestDate + ")");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step TaskletSecondStep() {
        return stepBuilderFactory.get("TaskletSecondStep")
                .tasklet(TaskletThirdStep(null))
                .build();
    }

    /**StepScope can use
     * 1. Tasklet
     * 2. ItemReader
     * 3. ItemWriter
     * 4. ItemProcessor
     * */
    @Bean
    @StepScope
    public Tasklet TaskletThirdStep(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return (contribution, chunkContext) -> {
            log.info("|-->[Configuration] TaskletConfiguration.TaskletThirdStep(" + requestDate + ")");
            return RepeatStatus.FINISHED;
        };
    }
}
