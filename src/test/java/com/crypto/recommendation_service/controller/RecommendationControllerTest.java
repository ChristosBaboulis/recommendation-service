package com.crypto.recommendation_service.controller;

import com.crypto.recommendation_service.dto.CryptoStatsResponse;
import com.crypto.recommendation_service.dto.NormalizedRangeResult;
import com.crypto.recommendation_service.exception.GlobalExceptionHandler;
import com.crypto.recommendation_service.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RecommendationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecommendationService service;

    @InjectMocks
    private RecommendationController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllByNormalizedRangeDesc_shouldReturnList() throws Exception {
        List<NormalizedRangeResult> mockResult = List.of(
                new NormalizedRangeResult("BTC", new BigDecimal("2.0")),
                new NormalizedRangeResult("ETH", new BigDecimal("1.5"))
        );

        when(service.getAllByNormalizedRangeDesc()).thenReturn(mockResult);

        mockMvc.perform(get("/cryptos/normalized"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("BTC"))
                .andExpect(jsonPath("$[0].normalizedRange").value(2.0));
    }

    @Test
    void getStatsBySymbol_shouldReturnStats() throws Exception {
        CryptoStatsResponse mockResponse = new CryptoStatsResponse(
                "BTC",
                new BigDecimal("100"),
                new BigDecimal("200"),
                new BigDecimal("50"),
                new BigDecimal("300")
        );

        when(service.getStatsForSymbol("BTC")).thenReturn(mockResponse);

        mockMvc.perform(get("/cryptos/BTC/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("BTC"))
                .andExpect(jsonPath("$.minPrice").value(50))
                .andExpect(jsonPath("$.maxPrice").value(300))
                .andExpect(jsonPath("$.oldestPrice").value(100))
                .andExpect(jsonPath("$.newestPrice").value(200));
    }

    @Test
    void getStatsBySymbol_shouldReturnBadRequestForUnknownSymbol() throws Exception {
        when(service.getStatsForSymbol("XYZ"))
                .thenThrow(new IllegalArgumentException("Unsupported or unknown symbol: XYZ"));

        mockMvc.perform(get("/cryptos/XYZ/stats"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unsupported or unknown symbol: XYZ"));
    }

    @Test
    void getHighestRangeForDate_shouldReturnCorrectSymbol() throws Exception {
        LocalDate date = LocalDate.of(2024, 5, 1);
        NormalizedRangeResult mockResult = new NormalizedRangeResult("BTC", new BigDecimal("1.25"));

        when(service.getCryptoWithHighestRangeOnDate(date)).thenReturn(mockResult);

        mockMvc.perform(get("/cryptos/highest-range")
                        .param("date", "2024-05-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("BTC"))
                .andExpect(jsonPath("$.normalizedRange").value(1.25));
    }
}
