# RealTime-Notification-Using-Redis-Spring
A highly scalable backend notification system built using Spring Boot, Redis, MySQL, and JWT Authentication.
It supports real-time message delivery with Redis Pub/Sub, API rate limiting, and role-based access control (RBAC).

Features: 
Real-time notification delivery using Redis Pub/Sub.

JWT authentication and Refresh Tokens for secure API access.

Role-Based Access Control (RBAC) to manage user permissions.

API Rate Limiting and Throttling using Redis and Spring AOP.

Resilient delivery with retry mechanisms for failed notifications.

Optimized database interactions with Spring Data JPA and DTO patterns.

Centralized Global Exception Handling for better error management.

Tech Stack:
Backend: Spring Boot, Spring Data JPA

Database: MySQL

Cache & Pub/Sub: Redis

Authentication: JWT, Spring Security

Build Tool: Maven

Testing: JUnit, Mockito

Project Architecture:
Client → API Gateway → Spring Boot Application → Redis Pub/Sub → Notification Processing → MySQL Database

Redis handles real-time messaging (Publisher/Subscriber model).

MySQL is used for persistent data storage (users, notifications, etc.).

API security is enforced using JWT Access and Refresh Tokens.

Throttling mechanisms control heavy API requests to maintain system performance.

SETUP:

# Clone the project
git clone https://github.com/your-username/real-time-notification-system.git
cd real-time-notification-system

# Create and configure application.properties file

# Suggestion: Create MySQL Database
# Check if Redis server is running
# Build the project
echo "Building the project..."
mvn clean install

# Run the project
echo "Running the Spring Boot application..."
mvn spring-boot:run

Manual Configuration Needed:
Before running the application, update your src/main/resources/application.properties file like this:
spring.datasource.url= YOUR DB URL
spring.datasource.username= YOUR DB USERNAME
spring.datasource.password= YOUR DB PASSWORD

spring.redis.host=localhost
spring.redis.port=6379

jwt.secret = your_jwt_secret_key

