package com.crypto.recommendation_service.service;

import com.crypto.recommendation_service.config.CryptoProperties;
import com.crypto.recommendation_service.model.CryptoEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoCSVLoader {
    private static final int BATCH_SIZE = 1000;
    private final CryptoProperties cryptoProperties;
    private final CryptoEntryService entryService;

    public void loadAndSaveAll() {
        List<String> symbols = List.of("BTC", "ETH", "LTC", "DOGE", "XRP");
        String folderPath = cryptoProperties.getCsvFolder();

        for (String symbol : symbols) {
            Path csvPath = Path.of(folderPath, symbol + "_values.csv");
            parseAndSaveBatches(csvPath.toString(), symbol);
        }
    }

    private void parseAndSaveBatches(String filePath, String symbol) {
        try (FileReader fileReader = new FileReader(filePath)) {
            CSVParser parser = CSVFormat.DEFAULT
                    .withHeader()
                    .withSkipHeaderRecord(true)
                    .parse(fileReader);

            List<CryptoEntry> batch = new ArrayList<>();

            for (CSVRecord record : parser) {
                long epochMillis = Long.parseLong(record.get("timestamp"));
                LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
                BigDecimal price = new BigDecimal(record.get("price"));

                batch.add(new CryptoEntry(timestamp, symbol, price));

                if (batch.size() == BATCH_SIZE) {
                    entryService.saveAll(batch);
                    log.info("Saved batch of {} for {}", batch.size(), symbol);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                entryService.saveAll(batch);
                log.info("Saved final batch of {} for {}", batch.size(), symbol);
            }

        } catch (Exception e) {
            log.error("Failed to parse CSV: {}", filePath, e);
        }
    }
}
