## API Endpoint Specifications

Below are the available REST endpoints exposed by the Recommendation Service, including descriptions and example request/response payloads.

---

### GET `/cryptos/normalized`

Returns a descending sorted list of all cryptos based on their normalized range.

**Description:**  
The normalized range is calculated as `(max - min) / min` for each crypto.

**Example Request:**  
```
GET http://localhost:8080/cryptos/normalized
```

**Example Response:**  
```json
[
  {
    "symbol": "BTC",
    "normalizedRange": 1.47352941
  },
  {
    "symbol": "ETH",
    "normalizedRange": 1.11231287
  }
]
```

---

### GET `/cryptos/{symbol}/stats`

Returns statistical values for a specific crypto symbol.

**Description:**  
Includes oldest price, newest price, minimum, and maximum price based on the available dataset.

**Example Request:**  
```
GET http://localhost:8080/cryptos/BTC/stats
```

**Example Response:**  
```json
{
  "symbol": "BTC",
  "oldestPrice": 46813.21,
  "newestPrice": 50200.67,
  "minPrice": 45000.00,
  "maxPrice": 51000.45
}
```

**Error Example (unknown symbol):**  
```
Status: 400 Bad Request
Body: "Unsupported or unknown symbol: XYZ"
```

---

### GET `/cryptos/highest-range?date=YYYY-MM-DD`

Returns the crypto with the highest normalized range for a specific day.

**Description:**  
The endpoint analyzes prices per symbol for the given day only and computes normalized range per symbol.

**Example Request:**  
```
GET http://localhost:8080/cryptos/highest-range?date=2022-01-05
```

**Example Response:**  
```json
{
  "symbol": "BTC",
  "normalizedRange": 0.98172727
}
```

**Error Example (no data for date):**  
```
Status: 400 Bad Request
Body: "No crypto data found for date: 2022-01-01"
```