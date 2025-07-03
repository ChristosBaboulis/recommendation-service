package com.crypto.recommendation_service.service;

import com.crypto.recommendation_service.model.CryptoEntry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CryptoDataInitializer {

    private final CryptoCSVLoader csvLoader;
    private final CryptoEntryService entryService;

    @PostConstruct
    public void init() {
        Map<String, List<CryptoEntry>> dataMap = csvLoader.loadAll();

        dataMap.forEach((symbol, entries) -> {
            entryService.saveAll(entries);
            log.info("Inserted {} {} entries to DB", entries.size(), symbol);
        });
    }
}
