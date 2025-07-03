package com.crypto.recommendation_service.service;

import com.crypto.recommendation_service.model.CryptoEntry;
import com.crypto.recommendation_service.repository.CryptoEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CryptoEntryService {

    private final CryptoEntryRepository repository;

    public List<CryptoEntry> getAllEntries() {
        return repository.findAll();
    }

    public CryptoEntry saveEntry(CryptoEntry entry) {
        return repository.save(entry);
    }

    public List<CryptoEntry> getEntriesWithinDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return repository.findAllByTimestampBetween(start, end);
    }

    public void saveAll(List<CryptoEntry> entries) {
        repository.saveAll(entries);
    }

    public Stream<CryptoEntry> streamEntriesBySymbol(String symbol) {
        return repository.streamBySymbol(symbol);
    }
}
