package boot.jpa.batch.job;

import boot.jpa.batch.domain.Hero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProcessorNullJobConfiguration {

    public static final String JOB_NAME = "processorNullBatch";
    public static final String BEAN_PREFIX = JOB_NAME + "_";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory emf;

    @Value("${chunkSize:1000}")
    private int chunkSize;

    /**How to run this job only
     * 1. shift + shift
     * 2. Search Edit Configurations
     * 3. BatchApplication
     * 4. Environment
     * 5. Program arguments : --job.name=processorNullBatch
     * 6. application.yml : spring:batch:job:names: ${job.name:NONE}
     * */
    @Bean(JOB_NAME)
    public Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .preventRestart()
                .start(step())
                .build();
    }

    @Bean(BEAN_PREFIX + "step")
    @JobScope
    public Step step() {
        return stepBuilderFactory.get(BEAN_PREFIX + "step")
                .<Hero, Hero>chunk(chunkSize)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Hero> reader() {
        return new JpaPagingItemReaderBuilder<Hero>()
                .name(BEAN_PREFIX+"reader")
                .entityManagerFactory(emf)
                .pageSize(chunkSize)
                .queryString("SELECT h FROM Hero h")
                .build();
    }

    @Bean
    public ItemProcessor<Hero, Hero> processor() {
        return hero -> {

            boolean isIgnoreTarget = hero.getId() % 2 == 0L;
            if(isIgnoreTarget){
                log.info("Hero name={}, isIgnoreTarget={}", hero.getName(), isIgnoreTarget);
                return null;
            }

            return hero;
        };
    }

    private ItemWriter<Hero> writer() {
        return items -> {
            for (Hero item : items) {
                log.info("Hero Name={}", item.getName());
            }
        };
    }
}