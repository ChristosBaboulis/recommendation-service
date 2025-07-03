package com.crypto.recommendation_service.service;

import com.crypto.recommendation_service.config.CryptoProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.Mockito.*;

class CryptoCSVLoaderBatchTest {

    private CryptoEntryService entryService;
    private CryptoCSVLoader loader;
    private Path tempFolder;

    @BeforeEach
    void setUp() throws Exception {
        entryService = mock(CryptoEntryService.class);
        CryptoProperties props = new CryptoProperties();
        tempFolder = Files.createTempDirectory("test-data");
        props.setCsvFolder(tempFolder.toString());
        loader = new CryptoCSVLoader(props, entryService);

        // Create sample CSV for BTC
        try (FileWriter writer = new FileWriter(tempFolder.resolve("BTC_values.csv").toFile())) {
            writer.write("timestamp,symbol,price\n");
            writer.write("1641009600000,BTC,46813.21\n");
            writer.write("1641013200000,BTC,47000.00\n");
        }

        // Create empty CSV for DOGE
        try (FileWriter writer = new FileWriter(tempFolder.resolve("DOGE_values.csv").toFile())) {
            writer.write("timestamp,symbol,price\n");
        }

        // Create CSVs for the rest to avoid file not found
        for (String symbol : List.of("ETH", "LTC", "XRP")) {
            try (FileWriter writer = new FileWriter(tempFolder.resolve(symbol + "_values.csv").toFile())) {
                writer.write("timestamp,symbol,price\n");
                writer.write("1641009600000," + symbol + ",100.00\n");
            }
        }
    }

    @Test
    void testLoadAndSaveAll() {
        loader.loadAndSaveAll();
        verify(entryService, atLeastOnce()).saveAll(anyList());
    }
}
