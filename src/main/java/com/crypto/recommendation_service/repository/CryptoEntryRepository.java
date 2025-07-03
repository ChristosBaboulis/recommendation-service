package com.crypto.recommendation_service.repository;

import com.crypto.recommendation_service.model.CryptoEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CryptoEntryRepository extends JpaRepository<CryptoEntry, Long> {
    List<CryptoEntry> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
