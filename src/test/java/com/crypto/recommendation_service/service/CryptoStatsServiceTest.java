package com.crypto.recommendation_service.service;

import com.crypto.recommendation_service.model.CryptoStats;
import com.crypto.recommendation_service.repository.CryptoStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CryptoStatsServiceTest {

    private CryptoStatsRepository repository;
    private CryptoStatsService service;

    @BeforeEach
    void setUp() {
        repository = mock(CryptoStatsRepository.class);
        service = new CryptoStatsService(repository);
    }

    @Test
    void saveStats_shouldInsertWhenSymbolNotExists() {
        CryptoStats newStats = new CryptoStats(null, "ETH",
                new BigDecimal("1000"), new BigDecimal("1500"),
                new BigDecimal("950"), new BigDecimal("1600"));

        when(repository.findBySymbol("ETH")).thenReturn(Optional.empty());

        service.saveStats(newStats);

        ArgumentCaptor<CryptoStats> captor = ArgumentCaptor.forClass(CryptoStats.class);
        verify(repository).save(captor.capture());

        CryptoStats saved = captor.getValue();
        assertThat(saved.getId()).isNull();
        assertThat(saved.getSymbol()).isEqualTo("ETH");
    }

    @Test
    void saveStats_shouldUpdateWhenSymbolExists() {
        CryptoStats existing = new CryptoStats(1L, "BTC",
                new BigDecimal("30000"), new BigDecimal("35000"),
                new BigDecimal("29000"), new BigDecimal("36000"));

        CryptoStats updated = new CryptoStats(null, "BTC",
                new BigDecimal("31000"), new BigDecimal("34000"),
                new BigDecimal("30500"), new BigDecimal("34500"));

        when(repository.findBySymbol("BTC")).thenReturn(Optional.of(existing));

        service.saveStats(updated);

        ArgumentCaptor<CryptoStats> captor = ArgumentCaptor.forClass(CryptoStats.class);
        verify(repository).save(captor.capture());

        CryptoStats saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getSymbol()).isEqualTo("BTC");
        assertThat(saved.getMinPrice()).isEqualByComparingTo("30500");
    }

    @Test
    void getStatsBySymbol_shouldReturnStatsWhenExists() {
        CryptoStats stats = new CryptoStats(1L, "BTC",
                new BigDecimal("10000"), new BigDecimal("15000"),
                new BigDecimal("9000"), new BigDecimal("16000"));

        when(repository.findBySymbol("BTC")).thenReturn(Optional.of(stats));

        CryptoStats result = service.getStatsBySymbol("BTC");

        assertThat(result.getSymbol()).isEqualTo("BTC");
        assertThat(result.getOldestPrice()).isEqualByComparingTo("10000");
    }

    @Test
    void getStatsBySymbol_shouldThrowWhenNotExists() {
        when(repository.findBySymbol("DOGE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getStatsBySymbol("DOGE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No stats found for symbol: DOGE");
    }

    @Test
    void saveAllStats_shouldSaveAllRecords() {
        List<CryptoStats> statsList = List.of(
                new CryptoStats(null, "BTC", new BigDecimal("1000"), new BigDecimal("1500"), new BigDecimal("950"), new BigDecimal("1600")),
                new CryptoStats(null, "ETH", new BigDecimal("2000"), new BigDecimal("2500"), new BigDecimal("1950"), new BigDecimal("2600"))
        );

        service.saveAllStats(statsList);

        verify(repository, times(2)).save(any());
    }

    @Test
    void getAllStats_shouldReturnAllStats() {
        List<CryptoStats> mockStats = List.of(
                new CryptoStats(1L, "BTC", new BigDecimal("10000"), new BigDecimal("15000"), new BigDecimal("9500"), new BigDecimal("16000")),
                new CryptoStats(2L, "ETH", new BigDecimal("2000"), new BigDecimal("2500"), new BigDecimal("1950"), new BigDecimal("2550"))
        );

        when(repository.findAll()).thenReturn(mockStats);

        List<CryptoStats> result = service.getAllStats();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSymbol()).isEqualTo("BTC");
        assertThat(result.get(1).getSymbol()).isEqualTo("ETH");
        verify(repository).findAll();
    }

}
