package com.crypto.recommendation_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoStats {
    private String symbol;
    private BigDecimal oldestPrice;
    private BigDecimal newestPrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
