package com.crypto.recommendation_service.repository;

import com.crypto.recommendation_service.model.CryptoStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CryptoStatsRepositoryTest {

    @Autowired
    private CryptoStatsRepository repository;

    @Test
    @DisplayName("Should save and retrieve CryptoStats")
    void testSaveAndRetrieve() {
        CryptoStats stats = new CryptoStats(
                "BTC",
                new BigDecimal("10000.00"),
                new BigDecimal("30000.00"),
                new BigDecimal("9000.00"),
                new BigDecimal("35000.00")
        );

        repository.save(stats);

        List<CryptoStats> all = repository.findAll();
        assertThat(all).hasSize(1);

        CryptoStats saved = all.get(0);
        assertThat(saved.getSymbol()).isEqualTo("BTC");
        assertThat(saved.getOldestPrice()).isEqualByComparingTo("10000.00");
        assertThat(saved.getNewestPrice()).isEqualByComparingTo("30000.00");
        assertThat(saved.getMinPrice()).isEqualByComparingTo("9000.00");
        assertThat(saved.getMaxPrice()).isEqualByComparingTo("35000.00");
    }
}
