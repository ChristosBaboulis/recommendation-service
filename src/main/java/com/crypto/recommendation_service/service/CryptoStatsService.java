package com.crypto.recommendation_service.service;

import com.crypto.recommendation_service.model.CryptoStats;
import com.crypto.recommendation_service.repository.CryptoStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CryptoStatsService {

    private final CryptoStatsRepository repository;

    public CryptoStats getStatsBySymbol(String symbol) {
        return repository.findBySymbol(symbol)
                .orElseThrow(() -> new IllegalArgumentException("No stats found for symbol: " + symbol));
    }

    public void saveStats(CryptoStats stats) {
        repository.findBySymbol(stats.getSymbol())
                .ifPresent(existing -> stats.setId(existing.getId()));

        repository.save(stats);
    }

    public void saveAllStats(List<CryptoStats> statsList) {
        for (CryptoStats stats : statsList) {
            saveStats(stats);
        }
    }

    public List<CryptoStats> getAllStats() {
        return repository.findAll();
    }
}
