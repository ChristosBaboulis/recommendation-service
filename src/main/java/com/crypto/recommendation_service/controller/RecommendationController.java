package com.crypto.recommendation_service.controller;

import com.crypto.recommendation_service.dto.CryptoStatsResponse;
import com.crypto.recommendation_service.dto.NormalizedRangeResult;
import com.crypto.recommendation_service.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cryptos")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    /*
     * GET /cryptos/normalized
     * Returns a descending sorted list of cryptos by normalized range
     */
    @GetMapping("/normalized")
    public ResponseEntity<List<NormalizedRangeResult>> getAllByNormalizedRangeDesc(){
        List<NormalizedRangeResult> result = recommendationService.getAllByNormalizedRangeDesc();
        return ResponseEntity.ok(result);
    }

    /*
     * GET /cryptos/{symbol}/stats
     * Returns oldest, newest, min, max for a specific crypto symbol
     */
    @GetMapping("/{symbol}/stats")
    public ResponseEntity<CryptoStatsResponse> getStatsBySymbol(@PathVariable String symbol){
        CryptoStatsResponse response = recommendationService.getStatsForSymbol(symbol);
        return ResponseEntity.ok(response);
    }

    /*
     * GET /cryptos/highest-range?date=YYYY-MM-DD
     * Returns symbol with highest normalized range on the given date
     */
    @GetMapping("/highest-range")
    public ResponseEntity<NormalizedRangeResult> getHighestRangeForDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        NormalizedRangeResult result = recommendationService.getCryptoWithHighestRangeOnDate(date);
        return ResponseEntity.ok(result);
    }
}
