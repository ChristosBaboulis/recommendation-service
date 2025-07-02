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
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CryptoProperties cryptoProperties;

    public Map<String, List<CryptoEntry>> loadAll(){
        Map<String, List<CryptoEntry>> dataMap = new HashMap<>();

        try{
            List<String> symbols = List.of("BTC", "ETH", "LTC", "DOGE", "XRP");
            String folderPath = cryptoProperties.getCsvFolder();

            for(String symbol : symbols){
                List<CryptoEntry> cryptoEntries = loadForSymbol(symbol);
                dataMap.put(symbol, cryptoEntries);
                log.info("Loaded {} records for {}", cryptoEntries.size(), symbol);
            }
        } catch (Exception e){
            log.error("Error loading CSV files", e);
        }

        return dataMap;
    }

    private List<CryptoEntry> loadForSymbol(String symbol) {
        Path csvPath = Path.of(cryptoProperties.getCsvFolder(), symbol + "_values.csv");
        return parseCsv(csvPath.toString());
    }

    private List<CryptoEntry> parseCsv(String filePath) {
        List<CryptoEntry> cryptoEntries = new ArrayList<>();

        try (FileReader fileReader = new FileReader(filePath)) {
            CSVFormat format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            CSVParser parser = format.parse(fileReader);

            for (CSVRecord record : parser) {
                long epochMillis = Long.parseLong(record.get("timestamp"));
                LocalDateTime timestamp = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());

                String symbol = record.get("symbol");

                BigDecimal price = new BigDecimal(record.get("price"));

                cryptoEntries.add(new CryptoEntry(timestamp, symbol, price));
            }

        } catch (Exception e) {
            log.error("Failed to parse CSV: {}", filePath, e);
        }

        return cryptoEntries;
    }
}
