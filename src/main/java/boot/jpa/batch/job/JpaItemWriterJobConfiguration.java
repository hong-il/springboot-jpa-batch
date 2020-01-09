package boot.jpa.batch.job;

import boot.jpa.batch.domain.Hero;
import boot.jpa.batch.domain.Hero2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaItemWriterJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private static final int chunkSize = 10;

    /**How to run this job only
     * 1. shift + shift
     * 2. Search Edit Configurations
     * 3. BatchApplication
     * 4. Environment
     * 5. Program arguments : --job.name=jpaItemWriterJob
     * 6. application.yml : spring:batch:job:names: ${job.name:NONE}
     * */
    @Bean
    public Job jpaItemWriterJob() {
        return jobBuilderFactory.get("jpaItemWriterJob")
                .start(jpaItemWriterStep())
                .build();
    }

    @Bean
    public Step jpaItemWriterStep() {
        return stepBuilderFactory.get("jpaItemWriterStep")
                .<Hero, Hero2>chunk(chunkSize)
                .reader(jpaItemWriterReader())
                .processor(jpaItemProcessor())
                .writer(jpaItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Hero> jpaItemWriterReader() {
        return new JpaPagingItemReaderBuilder<Hero>()
                .name("jpaItemWriterReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT h FROM Hero h")
                .build();
    }

    /**The Processor is required when data read from the Reader needs to be processed
     * */
    @Bean
    public ItemProcessor<Hero, Hero2> jpaItemProcessor() {
        return hero -> Hero2.builder()
                .name(hero.getName())
                .age(hero.getAge())
                .createdDate(hero.getCreatedDate())
                .build();
    }

    /**1. Saves the received Entity to the database
     * 2. Entity class must be accepted as generic type
     * */
    @Bean
    public JpaItemWriter<Hero2> jpaItemWriter() {
        JpaItemWriter<Hero2> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}