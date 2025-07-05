package com.crypto.recommendation_service.service;

import com.crypto.recommendation_service.dto.CryptoStatsResponse;
import com.crypto.recommendation_service.dto.NormalizedRangeResult;
import com.crypto.recommendation_service.model.CryptoEntry;
import com.crypto.recommendation_service.model.CryptoStats;
import com.crypto.recommendation_service.dto.CryptoStatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final CryptoEntryService entryService;
    private final CryptoStatsMapper statsMapper;

    @Transactional(readOnly = true)
    public List<NormalizedRangeResult> getAllByNormalizedRangeDesc() {
        List<CryptoStats> statsList = calculateStatsPerSymbol();

        return statsList.stream()
                .map(stats -> new NormalizedRangeResult(
                        stats.getSymbol(),
                        calculateNormalizedRange(stats)
                ))
                .sorted((a, b) -> b.getNormalizedRange().compareTo(a.getNormalizedRange()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CryptoStatsResponse getStatsForSymbol(String symbol) {
        CryptoStats stats = calculateStatsPerSymbol().stream()
                .filter(s -> s.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported or unknown symbol: " + symbol));

        return statsMapper.toDto(stats);
    }

    public NormalizedRangeResult getCryptoWithHighestRangeOnDate(LocalDate date) {
        List<CryptoEntry> entries = entryService.getEntriesWithinDate(date);

        return entries.stream()
                .collect(Collectors.groupingBy(CryptoEntry::getSymbol))
                .entrySet()
                .stream()
                .map(entry -> {
                    BigDecimal min = entry.getValue().stream().map(CryptoEntry::getPrice).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                    BigDecimal max = entry.getValue().stream().map(CryptoEntry::getPrice).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                    BigDecimal normalized = max.subtract(min).divide(min, 8, RoundingMode.HALF_UP);
                    return new NormalizedRangeResult(entry.getKey(), normalized);
                })
                .max(Comparator.comparing(NormalizedRangeResult::getNormalizedRange))
                .orElseThrow(() -> new IllegalArgumentException("No crypto data found for date: " + date));
    }

    //HELPERS
    private List<CryptoStats> calculateStatsPerSymbol() {
        List<String> supportedSymbols = entryService.getAllSymbols();
        List<CryptoStats> statsList = new ArrayList<>();

        for (String symbol : supportedSymbols) {
            try (Stream<CryptoEntry> stream = entryService.streamEntriesBySymbol(symbol)) {
                Iterator<CryptoEntry> iterator = stream.iterator();
                if (!iterator.hasNext()) continue;

                CryptoEntry first = iterator.next();
                BigDecimal min = first.getPrice();
                BigDecimal max = first.getPrice();
                BigDecimal oldest = first.getPrice();   //Stream is returned sorted from repo -> 1st element is oldest
                BigDecimal newest = first.getPrice();

                while (iterator.hasNext()) {
                    CryptoEntry entry = iterator.next();
                    BigDecimal price = entry.getPrice();

                    // Update min/max
                    if (price.compareTo(min) < 0) min = price;
                    if (price.compareTo(max) > 0) max = price;

                    newest = price;
                }

                statsList.add(new CryptoStats(symbol, oldest, newest, min, max));
            }
        }
        return statsList;
    }

    private BigDecimal calculateNormalizedRange(CryptoStats stats) {
        return stats.getMaxPrice().subtract(stats.getMinPrice())
                .divide(stats.getMinPrice(), 8, RoundingMode.HALF_UP);
    }
}
