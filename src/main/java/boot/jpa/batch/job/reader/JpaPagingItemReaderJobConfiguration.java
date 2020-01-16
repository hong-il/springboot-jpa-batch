package boot.jpa.batch.job.reader;

import boot.jpa.batch.domain.Hero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPagingItemReaderJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private int chunkSize = 10;

    /**How to run this job only
     * 1. shift + shift
     * 2. Search Edit Configurations
     * 3. BatchApplication
     * 4. Environment
     * 5. Program arguments : --job.name=jpaPagingItemReaderJob
     * */
    @Bean
    public Job jpaPagingItemReaderJob() {
        return jobBuilderFactory.get("jpaPagingItemReaderJob")
                .start(jpaPagingItemReaderStep())
                .build();
    }

    /**Unlike hibernate, JPA does not support cursor-based database access.
     * */
    @Bean
    public Step jpaPagingItemReaderStep() {
        return stepBuilderFactory.get("jpaPagingItemReaderStep")
                .<Hero, Hero> chunk(chunkSize)
                .reader(jpaPagingItemReader())
                .writer(jpaPagingItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Hero> jpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<Hero>()
                .name("JpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT h FROM Hero h WHERE age >= 26")
                .build();
    }

    private ItemWriter<Hero> jpaPagingItemWriter() {
        return list -> {
            for (Hero hero : list) {
                log.info("Current Hero={}", hero);
            }
        };
    }
}
