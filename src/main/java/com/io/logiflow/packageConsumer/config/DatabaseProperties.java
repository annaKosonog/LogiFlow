package com.io.logiflow.packageConsumer.config;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Getter
public class DatabaseProperties {

    private final JdbcConnectionDetails details;

    public DatabaseProperties(JdbcConnectionDetails details) {
        this.details = details;
    }

    public String getUrl() { return details.getJdbcUrl(); }
    public String getUsername() { return details.getUsername(); }
    public String getPassword() { return details.getPassword(); }

}
