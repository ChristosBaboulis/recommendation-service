package com.crypto.recommendation_service.config;

import com.crypto.recommendation_service.dto.CryptoStatsMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public CryptoStatsMapper cryptoStatsMapper() {
        return new CryptoStatsMapper() {};
    }
}