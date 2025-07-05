package com.crypto.recommendation_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class CryptoDataInitializerTest {

    private CryptoCSVLoader csvLoader;
    private CryptoDataInitializer initializer;

    @BeforeEach
    void setUp() {
        csvLoader = mock(CryptoCSVLoader.class);
        initializer = new CryptoDataInitializer(csvLoader);
    }

    @Test
    void init_shouldTriggerCsvParsingAndSave() {
        doNothing().when(csvLoader).loadAndSaveAll();
        initializer.init();
        verify(csvLoader).loadAndSaveAll();
    }
}
