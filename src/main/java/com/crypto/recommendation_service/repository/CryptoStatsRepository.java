package com.crypto.recommendation_service.repository;

import com.crypto.recommendation_service.model.CryptoStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CryptoStatsRepository extends JpaRepository<CryptoStats, Long> {
    Optional<CryptoStats> findBySymbol(String symbol);
}
