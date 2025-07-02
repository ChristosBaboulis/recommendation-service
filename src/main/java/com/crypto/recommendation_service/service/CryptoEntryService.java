package com.crypto.recommendation_service.service;

import com.crypto.recommendation_service.model.CryptoEntry;
import com.crypto.recommendation_service.repository.CryptoEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
