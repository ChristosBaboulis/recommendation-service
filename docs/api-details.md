# API Internal Details - Recommendation Service

This document explains the internal structure and operation of the Recommendation Service, including component responsibilities, data flow, and service design.  
The service is designed to ingest historical crypto data from CSV files and expose endpoints for retrieving statistics and normalized range recommendations.

---

## Component Overview

### `service/CryptoDataInitializer`
Executed once during application startup using `@PostConstruct`.  
It loads CSV data into the H2 database using `CryptoCSVLoader` and persists it via `CryptoEntryService`.

---

### `service/CryptoCSVLoader`
Exposes the method `loadAndSaveAll()` which reads all CSV files located in the configured folder.  
Each file is processed in **fixed-size batches** (e.g., 1000 entries per batch) to avoid loading all data in memory.  
Supported symbols are discovered **dynamically** by scanning the CSV directory for files ending in `_values.csv`.  
Batches are persisted to H2 via `CryptoEntryService`.

---

### `config/CryptoProperties`
Holds the configuration key `crypto.csv-folder` which points to the folder that contains the CSV files.  
Example from `application.yml`:

```yaml
crypto:
  csv-folder: ./data
```

---

### `config/MapperConfig`
Declares a manual CryptoStatsMapper bean for mapping internal model objects to DTOs

---

### `config/RateLimitingFilter`
A custom filter that enforces IP-based rate limiting using [Bucket4j](https://github.com/bucket4j/bucket4j).  
The filter is configured to allow **10 requests per minute per IP address**. This helps mitigate abuse and fulfills the requirement:  
*"Malicious users will always exist, so it will be really beneficial if at least we can rate limit them (based on IP)."*

---

### `exception/GlobalExceptionHandler`
Handles runtime exceptions for REST controllers (currently only `IllegalArgumentException`).  
Returns `400 Bad Request` with the error message as plain text in the response body.

---

## Code Structure

| Layer                             | Classes                                                             |
|-----------------------------------|---------------------------------------------------------------------|
| **Model**                         | `CryptoEntry`, `CryptoStats`                                        |
| **Repository**                    | `CryptoEntryRepository`                                             |
| **Service** (DAL, Business Logic) | `CryptoEntryService`, `RecommendationService`                       |
| **DTO**                           | `CryptoStatsMapper`, `CryptoStatsResponse`, `NormalizedRangeResult` |
| **Controller**                    | `RecommendationController`                                          |
| **Exception**                     | `GlobalExceptionHandler`                                            |
| **Service** (Init Purpose)        | `CryptoDataInitializer`, `CryptoCSVLoader`                          |
| **Config**                        | `CryptoProperties`, `MapperConfig`, `RateLimitingFilter`            |
---

## Application Flow

```plaintext
On Startup:
├── CryptoDataInitializer
    └── CryptoCSVLoader.loadAndSaveAll()
        └── Parses CSVs from /data
            └── Saves entries in batches (e.g., 1000 rows per batch) to H2 via CryptoEntryService
```

---

## Example CSV Data Parsed

Each record contains:
- `timestamp` (as epoch ms)
- `symbol` (e.g., BTC, ETH)
- `price`

---

## Streaming Logic (Read Optimization)

- Crypto statistics are calculated per symbol using **JPA streaming** (e.g., via `Stream<CryptoEntry>`), which avoids loading all entries in memory.
- This enables the application to scale to very large datasets (e.g., per-second data over a year) without `OutOfMemoryError`.
- The batching strategy during CSV ingestion and the streaming read logic together ensure memory-efficient processing.

---

## Notes

- CSV ingestion is done once at application startup.

---