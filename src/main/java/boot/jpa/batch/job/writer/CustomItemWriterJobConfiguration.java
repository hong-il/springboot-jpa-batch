package boot.jpa.batch.job.writer;

import boot.jpa.batch.domain.Hero;
import boot.jpa.batch.domain.Hero2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CustomItemWriterJobConfiguration {
    
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private static final int chunkSize = 10;

    /**How to run this job only
     * 1. shift + shift
     * 2. Search Edit Configurations
     * 3. BatchApplication
     * 4. Environment
     * 5. Program arguments : --job.name=customItemWriterJob
     * 6. application.yml : spring:batch:job:names: ${job.name:NONE}
     * */
    @Bean
    public Job customItemWriterJob() {
        return jobBuilderFactory.get("customItemWriterJob")
                .start(customItemWriterStep())
                .build();
    }

    @Bean
    public Step customItemWriterStep() {
        return stepBuilderFactory.get("customItemWriterStep")
                .<Hero, Hero2>chunk(chunkSize)
                .reader(customItemWriterReader())
                .processor(customItemWriterProcessor())
                .writer(customItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Hero> customItemWriterReader() {
        return new JpaPagingItemReaderBuilder<Hero>()
                .name("customItemWriterReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT h FROM Hero h")
                .build();
    }

    @Bean
    public ItemProcessor<Hero, Hero2> customItemWriterProcessor() {
        return hero -> Hero2.builder()
                .name(hero.getName())
                .age(hero.getAge())
                .createdDate(hero.getCreatedDate())
                .build();
    }

    /**When do we need custom item writer?
     * example
     * 1. When data read from the Reader needs to be transferred to the RestTemplate to the external API.
     * 2. When you need to put a value on a single-ton object for temporary storage and comparison.
     * 3. When multiple Entities need to be saved at the same time
     * */
    @Bean
    public ItemWriter<Hero2> customItemWriter() {
        return items -> {
            for (Hero2 item : items) {
                System.out.println(item);
            }
        };
    }
}