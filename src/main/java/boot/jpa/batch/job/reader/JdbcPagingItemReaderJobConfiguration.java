package boot.jpa.batch.job.reader;

import boot.jpa.batch.domain.Hero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JdbcPagingItemReaderJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    private static final int chunkSize = 10;

    /**How to run this job only
     * 1. shift + shift
     * 2. Search Edit Configurations
     * 3. BatchApplication
     * 4. Environment
     * 5. Program arguments : --job.name=jdbcPagingItemReaderJob
     * */
    @Bean
    public Job jdbcPagingItemReaderJob() throws Exception{
        return jobBuilderFactory.get("jdbcPagingItemReaderJob")
                .start(jdbcPagingItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcPagingItemReaderStep() throws Exception{
        return stepBuilderFactory.get("jdbcPagingItemReaderStep")
                .<Hero, Hero>chunk(chunkSize)
                .reader(jdbcPagingItemReader())
                .writer(jdbcPagingItemWriter())
                .build();
    }

    /**parameterValues(age) must be consistent with setWhereClause(:age)
     * */
    @Bean
    public JdbcPagingItemReader<Hero> jdbcPagingItemReader() throws Exception {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("age", 0);

        return new JdbcPagingItemReaderBuilder<Hero>()
                .pageSize(chunkSize)
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Hero.class))
                .queryProvider(createQueryProvider())
                .parameterValues(parameterValues)
                .name("jdbcPagingItemReader")
                .build();
    }

    private ItemWriter<Hero> jdbcPagingItemWriter() {
        return list -> {
            for (Hero hero : list) {
                log.info("Current Hero={}", hero);
            }
        };
    }

    /**SqlPagingQueryProviderFactoryBean
     * 1. Auto Select from "DB2, DB2VSE, DB2ZOS, DB2AS400, DERBY, HSQL, H2,
     *    MYSQL, ORACLE, POSTGRES, SQLITE, SQLSERVER, SYBASE" providers
     * */
    @Bean
    public PagingQueryProvider createQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();

        sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
        sqlPagingQueryProviderFactoryBean.setSelectClause("id, name, age, created_date");
        sqlPagingQueryProviderFactoryBean.setFromClause("from hero");
        sqlPagingQueryProviderFactoryBean.setWhereClause("where age >= :age");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.DESCENDING);

        sqlPagingQueryProviderFactoryBean.setSortKeys(sortKeys);

        return sqlPagingQueryProviderFactoryBean.getObject();
    }
}
