package hello.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

/**
 * Database configuration
 * 이런 식으로 수기로 해주지 않아도 스프링 부트가 Auto Configuration을 통해서 빈으로 등록을 해준다.
 */
@Slf4j
//@Configuration
public class DbConfig {

    @Bean
    public DataSource dataSource() {
        log.info("DataSource bean created");
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setJdbcUrl("jdbc:h2:mem:testdb");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public TransactionManager transactionManager() {
        log.info("TransactionManager bean created");
        return new JdbcTransactionManager(dataSource());
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        log.info("JdbcTemplate bean created");
        return new JdbcTemplate(dataSource());
    }
}
