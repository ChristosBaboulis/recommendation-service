package com.crypto.recommendation_service.dto;

import com.crypto.recommendation_service.model.CryptoStats;

public interface CryptoStatsMapper {
    default CryptoStatsResponse toDto(CryptoStats stats) {
        if (stats == null) return null;

        return new CryptoStatsResponse(
                stats.getSymbol(),
                stats.getOldestPrice(),
                stats.getNewestPrice(),
                stats.getMinPrice(),
                stats.getMaxPrice()
        );
    }
}
