package com.crypto.recommendation_service.service;

import com.crypto.recommendation_service.model.CryptoEntry;
import com.crypto.recommendation_service.repository.CryptoEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CryptoEntryServiceTest {

    private CryptoEntryRepository repository;
    private CryptoEntryService service;

    @BeforeEach
    void setUp() {
        repository = mock(CryptoEntryRepository.class);
        service = new CryptoEntryService(repository);
    }

    @Test
    void getAllEntries_shouldReturnAllRecords() {
        List<CryptoEntry> mockEntries = List.of(
                new CryptoEntry(LocalDateTime.of(2022, 1, 1, 0, 0), "BTC", new BigDecimal("45000")),
                new CryptoEntry(LocalDateTime.of(2022, 1, 2, 0, 0), "ETH", new BigDecimal("3500"))
        );

        when(repository.findAll()).thenReturn(mockEntries);

        List<CryptoEntry> result = service.getAllEntries();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSymbol()).isEqualTo("BTC");
        assertThat(result.get(1).getSymbol()).isEqualTo("ETH");
        verify(repository).findAll();
    }

    @Test
    void saveEntry_shouldSaveAndReturnEntity() {
        CryptoEntry entry = new CryptoEntry(LocalDateTime.of(2022, 1, 3, 0, 0), "DOGE", new BigDecimal("0.14"));
        when(repository.save(entry)).thenReturn(entry);

        CryptoEntry saved = service.saveEntry(entry);

        assertThat(saved.getSymbol()).isEqualTo("DOGE");
        assertThat(saved.getPrice()).isEqualByComparingTo("0.14");
        verify(repository).save(entry);
    }
}
