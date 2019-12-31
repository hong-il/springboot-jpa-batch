package boot.jpa.batch.job;

import boot.jpa.batch.domain.Hero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
/**CursorItemReader does not run in jpa.
 * */
public class JdbcCursorItemReaderJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    private static final int chunkSize = 10;

    /**How to run this job only
     * 1. shift + shift
     * 2. Search Edit Configurations
     * 3. BatchApplication
     * 4. Environment
     * 5. Program arguments : --job.name=jdbcCursorItemReaderJob
     * 6. application.yml : spring:batch:job:names: ${job.name:NONE}
     * */
    @Bean
    public Job jdbcCursorItemReaderJob() {
        return jobBuilderFactory.get("jdbcCursorItemReaderJob")
                .start(jdbcCursorItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcCursorItemReaderStep() {
        return stepBuilderFactory.get("jdbcCursorItemReaderStep")
                .<Hero, Hero>chunk(chunkSize)
                .reader(jdbcCursorItemReader())
                .writer(jdbcCursorItemWriter())
                .build();
    }

    /**First, insert the data into the database(except h2-database)
     * */
    @Bean
    public JdbcCursorItemReader<Hero> jdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<Hero>()
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Hero.class))
                .sql("SELECT id, name, age, created_date FROM HERO")
                .name("jdbcCursorItemReader")
                .build();
    }

    @Bean
    public ItemWriter<Hero> jdbcCursorItemWriter() {
        return list -> {
            for (Hero hero : list) {
                log.info("Current Hero={}", hero);
            }
        };
    }
}
