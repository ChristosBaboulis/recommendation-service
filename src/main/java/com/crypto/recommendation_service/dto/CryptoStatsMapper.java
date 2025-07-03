package com.crypto.recommendation_service.dto;

import com.crypto.recommendation_service.dto.CryptoStatsResponse;
import com.crypto.recommendation_service.model.CryptoStats;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CryptoStatsMapper {
    CryptoStatsResponse toDto(CryptoStats stats);
}
