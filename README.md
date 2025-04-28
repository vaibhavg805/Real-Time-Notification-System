# Real-Time Notification System Using Redis & Spring Boot

A highly scalable backend notification system built using Spring Boot, Redis, MySQL, and JWT Authentication.  
It supports real-time message delivery with Redis Pub/Sub, API rate limiting, and role-based access control (RBAC).

## ✨ Features

- Real-time notification delivery using Redis Pub/Sub
- JWT authentication and Refresh Tokens for secure API access
- Role-Based Access Control (RBAC) to manage user permissions
- API Rate Limiting and Throttling using Redis and Spring AOP
- Resilient delivery with retry mechanisms for failed notifications
- Optimized database interactions with Spring Data JPA and DTO patterns
- Centralized Global Exception Handling for better error management

## 🛠 Tech Stack

- **Backend:** Spring Boot, Spring Data JPA
- **Database:** MySQL
- **Cache & Pub/Sub:** Redis
- **Authentication:** JWT, Spring Security
- **Build Tool:** Maven
- **Testing:** JUnit, Mockito

## 🏗️ Project Architecture

```
Client → API Gateway → Spring Boot Application → Redis Pub/Sub → Notification Processing → MySQL Database
```

- Redis handles real-time messaging (Publisher/Subscriber model).
- MySQL is used for persistent data storage (users, notifications, etc.).
- API security is enforced using JWT Access and Refresh Tokens.
- Throttling mechanisms control heavy API requests to maintain system performance.

## 🚀 Setup Instructions

### Clone the project

```bash
git clone https://github.com/your-username/real-time-notification-system.git
cd real-time-notification-system
```

### Manual Configurations

Create and update the `src/main/resources/application.properties` file:

```properties
spring.datasource.url= YOUR_DB_URL
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

spring.redis.host=localhost
spring.redis.port=6379

jwt.secret=your_jwt_secret_key
jwt.expiration=3600000
jwt.refresh.token.expiration=604800000
```

## ⚡ Setup Database & Redis

- **MySQL:**  
  Create a database manually:

```sql
CREATE DATABASE notification_db;
```

- **Redis:**  
  Ensure Redis Server is running locally on port **6379**.

## 🏃 Build & Run the Project

```bash
# Build the project
mvn clean install

# Run the project
mvn spring-boot:run
```

## 📢 Notes

- Make sure Redis and MySQL services are running before starting the application.
- If you face any CORS issues during API testing, configure CORS settings in the Spring Security config.
- Use tools like **Postman** to test the REST APIs.


## 🎯 Contact

For more information, reach out to the author via mail - VaibhavGangele15@gmail.com
