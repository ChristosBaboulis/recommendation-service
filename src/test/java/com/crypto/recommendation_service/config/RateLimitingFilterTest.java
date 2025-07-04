package com.crypto.recommendation_service.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RateLimitingFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRateLimit() throws Exception {
        int maxRequests = 10;

        for (int i = 0; i < maxRequests; i++) {
            mockMvc.perform(get("/cryptos/normalized"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/cryptos/normalized"))
                .andExpect(status().isTooManyRequests());
    }
}