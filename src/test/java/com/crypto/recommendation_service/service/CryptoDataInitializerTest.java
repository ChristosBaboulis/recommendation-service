package com.crypto.recommendation_service.service;

import com.crypto.recommendation_service.model.CryptoEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class CryptoDataInitializerTest {

    private CryptoCSVLoader csvLoader;
    private CryptoEntryService entryService;
    private CryptoDataInitializer initializer;

    @BeforeEach
    void setUp() {
        csvLoader = mock(CryptoCSVLoader.class);
        entryService = mock(CryptoEntryService.class);
        initializer = new CryptoDataInitializer(csvLoader);
    }

    @Test
    void init_shouldTriggerCsvParsingAndSave() {
        doNothing().when(csvLoader).loadAndSaveAll();
        initializer.init();
        verify(csvLoader).loadAndSaveAll();
    }
}
