package com.unvus.config.database;


import com.unvus.config.properties.DatabaseProperties;
import com.zaxxer.hikari.HikariConfig;
import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import net.sf.log4jdbc.Spy;
import net.sf.log4jdbc.tools.Log4JdbcCustomFormatter;
import net.sf.log4jdbc.tools.LoggingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.Arrays;

public abstract class UnvusDatabaseConfiguration {

    private final Logger log = LoggerFactory.getLogger(UnvusDatabaseConfiguration.class);

    @Inject
    private Environment env;

    @Bean
    public abstract DataSource dataSource();


    /**
     * 공용 기본 데이터 소스 속성 세팅 및 생성
     *
     * @param databaseProperties
     * @return
     */
    protected HikariConfig configureDataSource(DatabaseProperties databaseProperties) {
        log.debug("Configuring Datasource");
        if (databaseProperties.getUrl() == null) {
            log.error("Your database connection pool configuration is incorrect! The application" +
            " cannot start. Please check your Spring profile, current profiles are: {}",
            Arrays.toString(env.getActiveProfiles()));

            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }
        HikariConfig config = databaseProperties.getHikari();
        config.setUsername(databaseProperties.getUsername());
        config.setPassword(databaseProperties.getPassword());
        config.setJdbcUrl(databaseProperties.getUrl());
        config.setAutoCommit(false);

        return databaseProperties.getHikari();
    }

    abstract public DataSource realDataSource();

    public DataSource wrapWithLog4jdbc() {
        Log4jdbcProxyDataSource logDataSource = new Log4jdbcProxyDataSource(realDataSource());

        RemoveEmptyLineFormatter formatter = new RemoveEmptyLineFormatter();
        formatter.setLoggingType(LoggingType.MULTI_LINE);
        logDataSource.setLogFormatter(formatter);
        return logDataSource;
    }

}


class RemoveEmptyLineFormatter extends Log4JdbcCustomFormatter {

    public String sqlOccured(Spy spy, String methodCall, String rawSql) {
        rawSql = rawSql.replaceAll("(?m)^\\s+$", "");
        return super.sqlOccured(spy, methodCall, rawSql);
    }

    public String sqlOccured(Spy spy, String methodCall, String[] sqls) {
        String s = "";

        for(int i = 0; i < sqls.length; ++i) {
            s = s + this.sqlOccured(spy, methodCall, sqls[i]) + String.format("%n");
        }

        return s;
    }
}
