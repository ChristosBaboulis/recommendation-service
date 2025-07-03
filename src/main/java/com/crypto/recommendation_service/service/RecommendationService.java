package com.crypto.recommendation_service.service;

import com.crypto.recommendation_service.dto.CryptoStatsResponse;
import com.crypto.recommendation_service.dto.NormalizedRangeResult;
import com.crypto.recommendation_service.model.CryptoEntry;
import com.crypto.recommendation_service.model.CryptoStats;
import com.crypto.recommendation_service.dto.CryptoStatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final CryptoEntryService entryService;
    private final CryptoStatsMapper statsMapper;

    public List<CryptoStats> calculateMonthlyStats() {
        List<CryptoEntry> entries = entryService.getAllEntries();

        Map<String, List<CryptoEntry>> grouped = entries.stream()
                .collect(Collectors.groupingBy(CryptoEntry::getSymbol));

        List<CryptoStats> statsList = new ArrayList<>();

        for (Map.Entry<String, List<CryptoEntry>> entry : grouped.entrySet()) {
            String symbol = entry.getKey();
            List<CryptoEntry> values = entry.getValue();

            values.sort(Comparator.comparing(CryptoEntry::getTimestamp));

            BigDecimal oldest = values.getFirst().getPrice();
            BigDecimal newest = values.getLast().getPrice();
            BigDecimal min = values.stream().map(CryptoEntry::getPrice).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
            BigDecimal max = values.stream().map(CryptoEntry::getPrice).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);

            statsList.add(new CryptoStats(null, symbol, oldest, newest, min, max));
        }

        return statsList;
    }

    public List<NormalizedRangeResult> getAllByNormalizedRangeDesc() {
        List<CryptoStats> statsList = calculateMonthlyStats();

        return statsList.stream()
                .map(stats -> new NormalizedRangeResult(
                        stats.getSymbol(),
                        calculateNormalizedRange(stats)
                ))
                .sorted((a, b) -> b.getNormalizedRange().compareTo(a.getNormalizedRange()))
                .collect(Collectors.toList());
    }

    public CryptoStatsResponse getStatsForSymbol(String symbol) {
        CryptoStats stats = calculateMonthlyStats().stream()
                .filter(s -> s.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported or unknown symbol: " + symbol));

        return statsMapper.toDto(stats);
    }


    public String getCryptoWithHighestRangeOnDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<CryptoEntry> entries = entryService.getEntriesWithinDate(date);

        return entries.stream()
                .collect(Collectors.groupingBy(CryptoEntry::getSymbol))
                .entrySet()
                .stream()
                .map(entry -> {
                    BigDecimal min = entry.getValue().stream().map(CryptoEntry::getPrice).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                    BigDecimal max = entry.getValue().stream().map(CryptoEntry::getPrice).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                    BigDecimal normalized = max.subtract(min).divide(min, 8, RoundingMode.HALF_UP);
                    return Map.entry(entry.getKey(), normalized);
                })
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("No crypto data found for date: " + date));
    }

    private BigDecimal calculateNormalizedRange(CryptoStats stats) {
        return stats.getMaxPrice().subtract(stats.getMinPrice())
                .divide(stats.getMinPrice(), 8, RoundingMode.HALF_UP);
    }
}
