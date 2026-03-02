# Uber Clone Backend API 🚖

A robust, scalable, and fully featured backend REST API for a ride-hailing application like Uber. Built with **Spring Boot** and **PostgreSQL (PostGIS)**, this project handles the entire ride lifecycle, complex spatial queries for driver matching, dynamic pricing, wallet transactions, and secure JWT-based authentication.

**Live API Documentation (Swagger UI):** [Explore the Live API Here](http://uber-spring-boot-env.eba-ryqc7ugu.ap-south-1.elasticbeanstalk.com/swagger-ui/index.html)

---

## 🌟 Key Features

* **Geospatial Driver Matching:** Utilizes PostGIS spatial queries to instantly locate and match the nearest available drivers to a rider's exact coordinates.
* **Dynamic Ride Fares (Surge Pricing):** Implements intelligent fare calculation factoring in distance, base rates, and real-time surge multipliers.
* **Complete Ride Lifecycle:** Handles ride requests, driver acceptances, OTP verification, ride starting, and ride completion.
* **Digital Wallet & Payments:** Built-in digital wallet system supporting automated top-ups, ride deductions, and cash payments with a fully traceable transaction ledger.
* **Rating System:** Mutual 5-star rating system (Driver ↔ Rider) after ride completion to maintain platform quality.
* **Secure Authentication:** Role-based access control (Rider, Driver, Admin) secured by stateless JWT (JSON Web Tokens).

---

## 🛠️ Tech Stack & Infrastructure

* **Language:** Java 21
* **Framework:** Spring Boot 3.x, Spring Data JPA, Spring Security
* **Database:** PostgreSQL with PostGIS extension (for spatial routing)
* **API Documentation:** OpenAPI 3.0 / Swagger UI
* **Build Tool:** Maven
* **Cloud & Deployment (AWS):** * Live deployment on **AWS Elastic Beanstalk**
* Database hosted on **AWS RDS**
* Automated CI/CD via **AWS CodePipeline**



---

## 🧠 Design Patterns Utilized

This project heavily utilizes the **Strategy Design Pattern** to write clean, extensible, and maintainable code:

* **`DriverMatchingStrategy`**: Dynamically switches between matching riders with the `NearestDriver` or the `HighestRatedDriver`.
* **`RideFareCalculationStrategy`**: Swaps between `DefaultFare` and `SurgePricingFare` based on real-time platform demand.
* **`PaymentStrategy`**: Abstracts payment processing logic to seamlessly switch between `WalletPayment` and `CashPayment`.
* **Factory/Manager Classes**: Uses `RideStrategyManager` and `PaymentStrategyManager` to automatically inject the correct strategy at runtime.

---

## 🗄️ Core Entities

* **`User`**: The base entity for authentication and profile data.
* **`Rider` / `Driver**`: Profile entities linked to the User, containing specific metrics like aggregate ratings and availability status.
* **`RideRequest` / `Ride**`: Tracks the origin/destination `Point` coordinates, OTPs, fares, and current ride status (e.g., `PENDING`, `ACCEPTED`, `ONGOING`, `ENDED`).
* **`Wallet` / `WalletTransaction**`: Manages user balances and logs every credit/debit action.
* **`Payment`**: Handles the status and method of ride settlements.

---

## 🚀 Getting Started (Local Setup)

### Prerequisites

1. **Java 21** installed.
2. **PostgreSQL** installed and running.
3. **PostGIS extension** installed on your PostgreSQL database.

### 1. Database Setup

Create a new PostgreSQL database and enable the PostGIS extension:

```sql
CREATE DATABASE uber_db;
\c uber_db
CREATE EXTENSION postgis;

```

### 2. Environment Variables

The application requires certain environment variables to run. Create an `application.properties` (or inject them directly into your IDE):

```properties
DB_HOST_URL=localhost
DB_NAME=uber_db
DB_USERNAME=your_postgres_username
DB_PASSWORD=your_postgres_password
JWT_SECRET_KEY=generate_a_very_long_random_secret_key_here
SERVER_PORT=8080

```

### 3. Build and Run

Clone the repository and run the application using Maven:

```bash
git clone https://github.com/astitva2409/uber-app.git
cd uber-app
./mvnw clean install
./mvnw spring-boot:run

```

### 4. Access the API

Once the server is running, access the local Swagger documentation at:
`http://localhost:8080/swagger-ui/index.html`

---

## 🔒 Authentication Flow

To interact with the secured endpoints (e.g., requesting a ride):

1. Send a `POST` request to `/auth/signup` to register a new user.
2. Send a `POST` request to `/auth/login` with your credentials.
3. Copy the `accessToken` from the response.
4. Click the **"Authorize"** button in Swagger UI (or use Postman) and paste the token to unlock the secure routes.

---

## 👤 Author

**Astitva Singh** Backend Developer | Java & Spring Boot Enthusiast

[GitHub Profile](https://www.google.com/search?q=https://github.com/astitva2409)