Here is a complete, professional `README.md` file tailored to your project. It highlights the Uber-like architecture, the spatial database capabilities, and the robust testing suite you just built.

```markdown
# UberApp - Enterprise Grade Ride-Sharing Backend

A robust, high-performance backend system for a ride-sharing application built with **Spring Boot 3.4+** and **PostGIS**. This project implements core Uber-like features including real-time driver matching, dynamic surge pricing, and spatial coordinate-based logic.

---

## 🚀 Key Features

* **Rider & Driver Workflows:** Complete lifecycle from ride request to completion.
* **Geospatial Intelligence:** Uses **PostGIS** for high-accuracy distance calculation and finding nearest available drivers.
* **Advanced Matching Strategies:** Implements the Strategy Design Pattern to switch between "Nearest Driver" and "Highest Rated Driver" algorithms.
* **Dynamic Pricing:** Automated surge pricing based on peak hours (7 PM - 9 PM).
* **Wallet & Payments:** Internal wallet system with transaction tracking and support for multiple payment methods (Cash/Wallet).
* **Security:** Stateless JWT-based authentication with role-based access control (RIDER, DRIVER, ADMIN).

---

## 🛠 Tech Stack

* **Framework:** Spring Boot 3.5.8
* **Language:** Java 21 (Corretto)
* **Database:** PostgreSQL with **PostGIS** extension
* **Security:** Spring Security & JWT (JJWT)
* **Mapping:** ModelMapper & JTS (Java Topology Suite)
* **Testing:** JUnit 5, Mockito, AssertJ
* **Infrastructure Testing:** **Testcontainers** (Dockerized PostGIS for Integration Tests)

---

## 🧪 Testing & Quality Assurance

This project maintains a high-quality codebase with extensive test coverage across all layers.

### 🏠 The Testing Pyramid


### 1. Geospatial Integration Tests
We use **Testcontainers** to verify our complex native SQL queries against a live PostGIS container.
* Verified `ST_DWithin` and `ST_Distance` logic for driver matching.
* Ensured spatial queries handle Earth's curvature by casting geometries to **Geography**.

### 2. Strategy Logic Verification
* **Surge Pricing:** Unit tests ensure 2x multipliers apply correctly during peak hours.
* **Mocked Static Time:** Used `MockedStatic` to "freeze time" in tests, ensuring logic remains valid regardless of the actual system time.

### 3. Web & Validation Layer
* Full **MockMvc** coverage for controllers.
* Validated Global Exception Handling and standardized API response wrappers.

---

## 🏁 Getting Started

### Prerequisites
* Java 21
* Maven 3.x
* Docker (Required for running Integration Tests)

### Installation
1. Clone the repository:
   ```bash
   git clone [https://github.com/your-username/UberApp.git](https://github.com/your-username/UberApp.git)

```

2. Set up environment variables for JWT Secret and Database credentials.
3. Build the project:
```bash
mvn clean install

```



### Running Tests

To run the full suite of unit and integration tests:

```bash
mvn test

```

---

## 📂 Project Structure

* `src/main/java/.../entities`: JPA Entities for Riders, Drivers, Rides, and Wallets.
* `src/main/java/.../strategies`: Implementation of the Strategy Pattern for Fare and Matching.
* `src/main/java/.../security`: JWT Filters and Security Configurations.
* `src/test/java/...`: Robust test suite including Testcontainer configurations.

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

```

### One Final Step
Since you are pushing to GitHub, you might want to create a `.gitignore` file (if you haven't already) to make sure you don't push your `/target` folder or your `.idea` files. 

**Would you like me to generate a `.gitignore` for you as well?**

```