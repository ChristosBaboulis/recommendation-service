package com.crypto.recommendation_service.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class CryptoEntryTest {
    @Test
    void testCryptoEntryCreation(){
        LocalDateTime now = LocalDateTime.now();
        BigDecimal price = new BigDecimal("30000.50");
        CryptoEntry cryptoEntry = new CryptoEntry(now, "BTC", price);

        assertEquals("BTC", cryptoEntry.getSymbol());
        assertEquals(price, cryptoEntry.getPrice());
        assertEquals(now, cryptoEntry.getTimestamp());
    }
}
