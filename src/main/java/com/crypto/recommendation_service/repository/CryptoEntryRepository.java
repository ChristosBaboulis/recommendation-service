package com.crypto.recommendation_service.repository;

import com.crypto.recommendation_service.model.CryptoEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface CryptoEntryRepository extends JpaRepository<CryptoEntry, Long> {
    List<CryptoEntry> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT e FROM CryptoEntry e WHERE e.symbol = :symbol ORDER BY e.timestamp ASC")
    Stream<CryptoEntry> streamBySymbol(@Param("symbol") String symbol);
}
