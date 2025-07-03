package com.crypto.recommendation_service.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CryptoDataInitializer {

    private final CryptoCSVLoader loader;

    @PostConstruct
    public void init() {
        loader.loadAndSaveAll();
    }
}
