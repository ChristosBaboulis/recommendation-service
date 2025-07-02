package com.crypto.recommendation_service.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CryptoStatsTest {
    @Test
    void testCryptoStatsCreation() {
        CryptoStats stats = new CryptoStats(
                "ETH",
                new BigDecimal("1000.00"),
                new BigDecimal("1500.00"),
                new BigDecimal("900.00"),
                new BigDecimal("2000.00")
        );

        assertEquals("ETH", stats.getSymbol());
        assertEquals(new BigDecimal("1000.00"), stats.getOldestPrice());
        assertEquals(new BigDecimal("1500.00"), stats.getNewestPrice());
        assertEquals(new BigDecimal("900.00"), stats.getMinPrice());
        assertEquals(new BigDecimal("2000.00"), stats.getMaxPrice());
    }
}
