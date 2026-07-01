package com.sigep.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class DataSourceConfig {

    @Primary
    @Bean(name = "sigepDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.sigep")
    public DataSource sigepDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }
}
