package com.crypto.recommendation_service.repository;

import com.crypto.recommendation_service.model.CryptoEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CryptoEntryRepositoryTest {

    @Autowired
    private CryptoEntryRepository repository;

    @Test
    @DisplayName("Should save and retrieve CryptoEntry")
    void testSaveAndRetrieve() {
        CryptoEntry entry = new CryptoEntry( LocalDateTime.of(2022, 1, 1, 0, 0), "BTC", new BigDecimal("46813.21") );

        repository.save(entry);

        List<CryptoEntry> results = repository.findAll();
        assertThat(results).hasSize(1);

        CryptoEntry saved = results.get(0);
        assertThat(saved.getSymbol()).isEqualTo("BTC");
        assertThat(saved.getPrice()).isEqualByComparingTo("46813.21");
        assertThat(saved.getTimestamp()).isEqualTo(LocalDateTime.of(2022, 1, 1, 0, 0));
    }

    @Test
    @DisplayName("Should return entries between two timestamps")
    void findAllByTimestampBetween_shouldReturnCorrectEntries() {
        CryptoEntry entry1 = new CryptoEntry(LocalDateTime.of(2022, 1, 1, 10, 0), "BTC", new BigDecimal("40000"));
        CryptoEntry entry2 = new CryptoEntry(LocalDateTime.of(2022, 1, 2, 10, 0), "BTC", new BigDecimal("42000"));
        CryptoEntry entry3 = new CryptoEntry(LocalDateTime.of(2022, 1, 3, 10, 0), "BTC", new BigDecimal("43000"));

        repository.saveAll(List.of(entry1, entry2, entry3));

        LocalDateTime from = LocalDateTime.of(2022, 1, 2, 0, 0);
        LocalDateTime to = LocalDateTime.of(2022, 1, 3, 0, 0);

        List<CryptoEntry> results = repository.findAllByTimestampBetween(from, to);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getPrice()).isEqualByComparingTo("42000");
    }
}
