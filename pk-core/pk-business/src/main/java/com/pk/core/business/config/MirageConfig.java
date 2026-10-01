package com.pk.core.business.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import vn.com.unit.miragesql.miragesql.SqlManagerImpl;
import vn.com.unit.miragesql.miragesql.bean.BeanDescFactory;
import vn.com.unit.miragesql.miragesql.bean.FieldPropertyExtractor;
import vn.com.unit.miragesql.miragesql.dialect.Dialect;
import vn.com.unit.miragesql.miragesql.dialect.H2Dialect;
import vn.com.unit.miragesql.miragesql.dialect.PostgreSQLDialect;
import vn.com.unit.miragesql.miragesql.integration.spring.SpringConnectionProvider;
import vn.com.unit.miragesql.miragesql.naming.RailsLikeNameConverter;

/**
 * Cấu hình Mirage (2-way SQL) dùng chung cho mọi module: bean "sqlManager".
 * Dialect chọn bằng app.mirage.dialect (postgresql | h2), mặc định postgresql.
 * Khoá chính dùng SEQUENCE (PostgreSQLDialect của Mirage không hỗ trợ IDENTITY).
 */
@Configuration
public class MirageConfig {

    @Bean
    SqlManagerImpl sqlManager(DataSourceTransactionManager transactionManager,
                              @Value("${app.mirage.dialect:postgresql}") String dialectName) {
        SqlManagerImpl sqlManager = new SqlManagerImpl();
        sqlManager.setConnectionProvider(connectionProvider(transactionManager));
        sqlManager.setDialect(dialect(dialectName));
        sqlManager.setBeanDescFactory(beanDescFactory());
        sqlManager.setNameConverter(new RailsLikeNameConverter());
        return sqlManager;
    }

    @Bean
    SpringConnectionProvider connectionProvider(DataSourceTransactionManager transactionManager) {
        SpringConnectionProvider connectionProvider = new SpringConnectionProvider();
        connectionProvider.setTransactionManager(transactionManager);
        return connectionProvider;
    }

    @Bean
    BeanDescFactory beanDescFactory() {
        BeanDescFactory beanDescFactory = new BeanDescFactory();
        beanDescFactory.setPropertyExtractor(new FieldPropertyExtractor());
        return beanDescFactory;
    }

    private static Dialect dialect(String name) {
        return switch (name.toLowerCase()) {
            case "h2" -> new H2Dialect();
            case "postgresql", "postgres" -> new PostgreSQLDialect();
            default -> throw new IllegalArgumentException("app.mirage.dialect không hỗ trợ: " + name);
        };
    }
}
