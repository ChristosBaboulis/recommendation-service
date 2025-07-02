package com.crypto.recommendation_service.repository;

import com.crypto.recommendation_service.model.CryptoEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoEntryRepository extends JpaRepository<CryptoEntry, Long> {

}
