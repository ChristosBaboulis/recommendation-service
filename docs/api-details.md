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
Exposes the method `loadAndSaveAll()` which reads all 5 CSV files located in the configured folder.  
Each file is processed in **fixed-size batches** (e.g., 1000 entries per batch) to avoid loading all data in memory.  
Data loading is performed in **fixed-size batches (e.g., 1000 records)** for memory efficiency.  
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

### `exception/GlobalExceptionHandler`
Handles runtime exceptions for REST controllers (currently only `IllegalArgumentException`).  
Returns `400 Bad Request` with the error message as plain text in the response body.

---

## Code Structure

| Layer         | Classes                                                                 |
|---------------|-------------------------------------------------------------------------|
| **Model**     | `CryptoEntry`, `CryptoStats`                                            |
| **Repository**| `CryptoEntryRepository`                                                 |
| **Service**   | `CryptoEntryService`, `RecommendationService`                           |
| **DTO**       | `CryptoStatsMapper`, `CryptoStatsResponse`, `NormalizedRangeResult`     |
| **Controller**| `RecommendationController`                                              |
| **Exception** | `GlobalExceptionHandler`                                                |
| **Init**      | `CryptoDataInitializer`, `CryptoCSVLoader`, `CryptoProperties`          |

---

## Application Flow

```plaintext
On Startup:
├── CryptoDataInitializer
    └── CryptoCSVLoader.loadAndSaveAll()
        └── Parses 5 CSVs from /data
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