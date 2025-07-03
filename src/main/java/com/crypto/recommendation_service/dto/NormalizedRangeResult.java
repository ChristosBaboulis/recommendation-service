package com.crypto.recommendation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class NormalizedRangeResult {
    private String symbol;
    private BigDecimal normalizedRange;
}
