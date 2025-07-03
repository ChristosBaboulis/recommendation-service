package com.crypto.recommendation_service.service;

import com.crypto.recommendation_service.dto.CryptoStatsResponse;
import com.crypto.recommendation_service.dto.NormalizedRangeResult;
import com.crypto.recommendation_service.model.CryptoEntry;
import com.crypto.recommendation_service.model.CryptoStats;
import com.crypto.recommendation_service.dto.CryptoStatsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class RecommendationServiceTest {

    @Mock
    private CryptoEntryService entryService;

    @Mock
    private CryptoStatsMapper mapper;

    @InjectMocks
    private RecommendationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateStatsPerSymbol_shouldReturnCorrectStats() {
        LocalDateTime now = LocalDateTime.now();
        CryptoEntry e1 = new CryptoEntry(1L, now.minusDays(2), "BTC", new BigDecimal("50"));
        CryptoEntry e2 = new CryptoEntry(2L, now, "BTC", new BigDecimal("150"));

        when(entryService.streamEntriesBySymbol("BTC")).thenReturn(Stream.of(e1, e2));
        when(entryService.streamEntriesBySymbol("ETH")).thenReturn(Stream.empty());
        when(entryService.streamEntriesBySymbol("LTC")).thenReturn(Stream.empty());
        when(entryService.streamEntriesBySymbol("DOGE")).thenReturn(Stream.empty());
        when(entryService.streamEntriesBySymbol("XRP")).thenReturn(Stream.empty());

        List<CryptoStats> result = service.calculateStatsPerSymbol();

        assertThat(result).hasSize(1);
        CryptoStats stats = result.get(0);
        assertThat(stats.getSymbol()).isEqualTo("BTC");
        assertThat(stats.getMinPrice()).isEqualByComparingTo("50");
        assertThat(stats.getMaxPrice()).isEqualByComparingTo("150");
        assertThat(stats.getOldestPrice()).isEqualByComparingTo("50");
        assertThat(stats.getNewestPrice()).isEqualByComparingTo("150");
    }

    @Test
    void getAllByNormalizedRangeDesc_shouldReturnSortedResults() {
        CryptoEntry b1 = new CryptoEntry(1L, LocalDateTime.now(), "BTC", new BigDecimal("50"));
        CryptoEntry b2 = new CryptoEntry(2L, LocalDateTime.now(), "BTC", new BigDecimal("200"));
        CryptoEntry e1 = new CryptoEntry(3L, LocalDateTime.now(), "ETH", new BigDecimal("100"));
        CryptoEntry e2 = new CryptoEntry(4L, LocalDateTime.now(), "ETH", new BigDecimal("200"));

        when(entryService.streamEntriesBySymbol("BTC")).thenReturn(Stream.of(b1, b2));
        when(entryService.streamEntriesBySymbol("ETH")).thenReturn(Stream.of(e1, e2));
        when(entryService.streamEntriesBySymbol("LTC")).thenReturn(Stream.empty());
        when(entryService.streamEntriesBySymbol("DOGE")).thenReturn(Stream.empty());
        when(entryService.streamEntriesBySymbol("XRP")).thenReturn(Stream.empty());

        List<NormalizedRangeResult> result = service.getAllByNormalizedRangeDesc();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSymbol()).isEqualTo("BTC");
        assertThat(result.get(1).getSymbol()).isEqualTo("ETH");
    }

    @Test
    void getStatsForSymbol_shouldReturnMappedDto() {
        CryptoEntry e1 = new CryptoEntry(1L, LocalDateTime.now().minusDays(1), "BTC", BigDecimal.ONE);
        CryptoEntry e2 = new CryptoEntry(2L, LocalDateTime.now(), "BTC", BigDecimal.TEN);
        CryptoStats stats = new CryptoStats("BTC", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN);
        CryptoStatsResponse response = new CryptoStatsResponse("BTC", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN);

        when(entryService.streamEntriesBySymbol("BTC")).thenReturn(Stream.of(e1, e2));
        when(entryService.streamEntriesBySymbol("ETH")).thenReturn(Stream.empty());
        when(entryService.streamEntriesBySymbol("LTC")).thenReturn(Stream.empty());
        when(entryService.streamEntriesBySymbol("DOGE")).thenReturn(Stream.empty());
        when(entryService.streamEntriesBySymbol("XRP")).thenReturn(Stream.empty());
        when(mapper.toDto(stats)).thenReturn(response);

        CryptoStatsResponse result = service.getStatsForSymbol("BTC");

        assertEquals("BTC", result.getSymbol());
        assertThat(result.getMinPrice()).isEqualByComparingTo("1");
        assertThat(result.getMaxPrice()).isEqualByComparingTo("10");
        assertThat(result.getOldestPrice()).isEqualByComparingTo("1");
        assertThat(result.getNewestPrice()).isEqualByComparingTo("10");
    }

    @Test
    void getCryptoWithHighestRangeOnDate_shouldReturnCorrectResult() {
        LocalDate date = LocalDate.now();

        when(entryService.getEntriesWithinDate(date)).thenReturn(List.of(
                new CryptoEntry(1L, date.atStartOfDay(), "BTC", new BigDecimal("50")),
                new CryptoEntry(2L, date.atTime(23, 59), "BTC", new BigDecimal("150")),
                new CryptoEntry(3L, date.atStartOfDay(), "ETH", new BigDecimal("100")),
                new CryptoEntry(4L, date.atTime(23, 59), "ETH", new BigDecimal("120"))
        ));

        NormalizedRangeResult result = service.getCryptoWithHighestRangeOnDate(date);

        assertEquals("BTC", result.getSymbol());
        assertThat(result.getNormalizedRange()).isEqualByComparingTo("2.00000000");
    }

    @Test
    void getStatsForSymbol_shouldThrowWhenSymbolNotFound() {
        when(entryService.streamEntriesBySymbol("BTC")).thenReturn(Stream.empty());
        when(entryService.streamEntriesBySymbol("ETH")).thenReturn(Stream.empty());
        when(entryService.streamEntriesBySymbol("LTC")).thenReturn(Stream.empty());
        when(entryService.streamEntriesBySymbol("DOGE")).thenReturn(Stream.empty());
        when(entryService.streamEntriesBySymbol("XRP")).thenReturn(Stream.empty());

        assertThrows(IllegalArgumentException.class, () -> service.getStatsForSymbol("XYZ"));
    }
}