package com.crypto.recommendation_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "crypto_stats")
public class CryptoStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false, unique = true)
    private String symbol;

    @Column(name = "oldest_price", nullable = false, precision = 19, scale = 8)
    private BigDecimal oldestPrice;

    @Column(name = "newest_price", nullable = false, precision = 19, scale = 8)
    private BigDecimal newestPrice;

    @Column(name = "min_price", nullable = false, precision = 19, scale = 8)
    private BigDecimal minPrice;

    @Column(name = "max_price", nullable = false, precision = 19, scale = 8)
    private BigDecimal maxPrice;

    public CryptoStats(String symbol, BigDecimal oldestPrice, BigDecimal newestPrice, BigDecimal minPrice, BigDecimal maxPrice) {
        this.symbol = symbol;
        this.oldestPrice = oldestPrice;
        this.newestPrice = newestPrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}
