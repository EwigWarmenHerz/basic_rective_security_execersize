package com.ewig.database.configuration.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class GeneralConfig {
    @Value("${timezone.zone}")
    private  String timezone;
    @PostConstruct
    private void configTimezone(){;
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(timezone)));
    }
}
