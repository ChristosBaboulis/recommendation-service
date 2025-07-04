# Recommendation Service

This is a Spring Boot application that provides statistics and investment recommendations based on historical cryptocurrency prices.

---

## Technologies Used

- **Java 21**
- **Spring Boot 3.5**
- **Maven** (project structure and dependency management)
- **Docker** (containerizes the app for deployment, ready for Docker Compose & Kubernetes)
- **H2 Database** (in-memory DB for simplicity and repeatable tests)
- **Apache Commons CSV** (for parsing historical crypto price data)
- **SLF4J & Logback** (logging framework)
- **Lombok** (to reduce boilerplate code)
- **JUnit 5** (unit and integration testing)
- **GitHub Actions** (CI pipeline)
- **Java Stream API** (for data processing and transformation)


---

## Setup Instructions

You can run the Recommendation Service either using Maven (locally) or as a Docker container.

### Option 1: Run Locally with Maven

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/recommendation-service.git
   cd recommendation-service
   ```

2. **Run the application using Maven**
   ```bash
   mvn spring-boot:run
   ```

3. **H2 Console**  
   Access the in-memory database at: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    - JDBC URL: `jdbc:h2:mem:testdb`
    - Username: `sa`
    - Password: *(leave empty)*


4. **Testing with Postman**  
   Use the provided Postman collection located at:  
   `postman/RecommendationService.postman_collection.json`

### Option 2: Run as a Docker Container

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/recommendation-service.git
   cd recommendation-service
   ```
   
2. **Run the application using Docker**  
   First, build the Docker image:
   ```bash
   mvn clean package
   docker build -t recommendation-service .
   ```

   Then, run the container:
   ```bash
   docker run -p 8080:8080 recommendation-service
   ```
   The container includes the `data/` folder and uses the default configuration from `application.yml`, so no environment variables are required.


3. **H2 Console**  
    Access the in-memory database at: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    - JDBC URL: `jdbc:h2:mem:cryptodb`
    - Username: `sa`
    - Password: *(leave empty)*


4. **Testing with Postman**  
   Use the provided Postman collection located at:  
   `postman/RecommendationService.postman_collection.json`

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/cryptos/normalized` | Returns all cryptos sorted by normalized range (descending) |
| GET    | `/cryptos/{symbol}/stats` | Returns oldest, newest, min, max price for a specific crypto |
| GET    | `/cryptos/highest-range?date=YYYY-MM-DD` | Returns the crypto with the highest normalized range on a given date |

For detailed Endpoints specifications and example request/response bodies, see: [docs/api-endpoints-specifications.md](docs/api-endpoints-specifications.md)

---

## CSV Input

Upon application startup, historical data is automatically loaded from CSV files located under the ./data directory using batch processing to ensure memory efficiency.
The folder path is configured in application.yml under:

```yaml
crypto:
  csv-folder: ./data
```

Each CSV file **must follow the format**: `<SYMBOL>_values.csv`, e.g., `BTC_values.csv`.

Additionally, all rows **must contain records with matching `symbol` values** inside the CSV.  
For example, in `BTC_values.csv`, every row's `symbol` column must be `"BTC"`.

This convention ensures correct parsing and categorization of cryptocurrency entries.

---

## Design Considerations

- The application is designed to be **easily extensible**: to add a new crypto, simply add its CSV file using the naming convention.
- The service dynamically discovers supported cryptocurrencies at runtime by scanning the configured CSV directory for files ending in `_values.csv`. This removes the need for hardcoded symbol lists. Adding a new cryptocurrency is as simple as dropping a properly named file (e.g., `ADA_values.csv`) into the data folder. The system will automatically ingest and include it in all calculations.
- All CSV records are persisted to the database at startup using batched inserts (1000 records per batch), enabling efficient processing even for large datasets.
  Recommendations are computed on-demand from the persisted data.
- The application **guards against unsupported symbols** by validating them before computing statistics.
- Stream-based processing and batching ensure the application can scale to handle millions of records without exhausting memory.
- For large datasets (e.g., a full year of per-second crypto prices), the service processes data using JPA Stream, avoiding memory overload.

---

## Extra Mile (Optional Features)

- **Containerization (Kubernetes ready)**  
  The application is Dockerized using a standard `Dockerfile`, preparing it for deployment in Kubernetes environments.

- **Rate Limiting (per IP)**  
  To protect from malicious usage, a rate limiter such as Bucket4j or Spring Cloud Gateway can be added for IP throttling.

---

For detailed project structure, see: [docs/api-details.md](docs/api-details.md)
