package com.crypto.recommendation_service.service;

import com.crypto.recommendation_service.config.CryptoProperties;
import com.crypto.recommendation_service.model.CryptoEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CryptoCSVLoaderTest {

    private CryptoCSVLoader loader;

    @BeforeEach
    void setUp() {
        CryptoProperties props = new CryptoProperties();
        props.setCsvFolder("src/test/resources/test-data");
        loader = new CryptoCSVLoader(props);
    }

    @Test
    void testLoadBTCEntries() {
        Map<String, List<CryptoEntry>> allData = loader.loadAll();

        assertTrue(allData.containsKey("BTC"));
        List<CryptoEntry> btcList = allData.get("BTC");
        assertEquals(2, btcList.size());

        CryptoEntry first = btcList.getFirst();
        assertEquals("BTC", first.getSymbol());
        assertEquals(new BigDecimal("46813.21"), first.getPrice());
        assertEquals(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(1641009600000L), ZoneId.systemDefault()),
                first.getTimestamp()
        );
    }

    @Test
    void testMissingFileHandledGracefully() {
        Map<String, List<CryptoEntry>> allData = loader.loadAll();
        assertTrue(allData.containsKey("DOGE"));
        assertTrue(allData.get("DOGE").isEmpty() || allData.get("DOGE").size() == 0);
    }
}
