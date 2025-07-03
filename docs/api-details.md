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
Exposes the method `loadAll()` which reads all 5 CSV files located in the configured folder.  
The folder path is provided through `CryptoProperties` (`application.yml`).

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
    └── CryptoCSVLoader.loadAll()
        └── Parses 5 CSVs from /data
            └── Saves entries to H2 via CryptoEntryService
```

---

## Example CSV Data Parsed

Each record contains:
- `timestamp` (as epoch ms)
- `symbol` (e.g., BTC, ETH)
- `price`

---

## Notes

- CSV ingestion is done once at application startup.
- Only 5 cryptocurrencies are supported by default (`BTC`, `ETH`, `LTC`, `DOGE`, `XRP`).
- The service is designed to scale with additional symbols if needed in future.

---

## Recommendation

To extend:
- Move from fixed list of 5 symbols to dynamic discovery (e.g., file listing or DB config).
- Allow time-range parameterization for different recommendation needs (e.g., 1-month vs 6-month).
- Add caching or pagination if dataset grows large.