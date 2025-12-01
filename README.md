#  Flight Booking Microservices System

This project is a complete microservices-based Flight Booking System built using:

- **Spring Boot**
- **Spring Cloud (Eureka, Config Server, API Gateway)**
- **OpenFeign**
- **Kafka (Message Broker)**
- **MySQL Databases**
- **Docker & Docker Compose**

---

##  Microservices Architecture

The system contains the following services:

| Service | Port | Description |
|--------|------|-------------|
| **Service Registry (Eureka)** | 8761 | Service discovery |
| **API Gateway** | 8080 | Entry point for all client requests |
| **Config Server** | 8888 | Centralized configuration |
| **Flight Service** | 8081 | Manages flights & inventory |
| **Booking Service** | 8082 | Handles booking workflows & produces Kafka events |
| **Email Service** | 8085 | Consumes Kafka events & sends confirmation emails |
| **MySQL Databases** | 3036 | Separate DBs for booking & flight data |
| **Kafka + Zookeeper** | 9092 | Event streaming |

---

##  How to Run the Project (Docker)

### **1️ Install required tools**
- Docker Desktop  
- Java 17  
- Maven 3.8+  

---

## **2️ Build all microservices**

Run this command inside each service folder:

```sh
mvn clean package -DskipTests
```

---

## **3️ Start Kafka + Zookeeper**

Create a file named `docker-compose.yml`:

```yaml
version: '3'
services:
  zookeeper:
    image: wurstmeister/zookeeper
    ports:
      - "2181:2181"

  kafka:
    image: wurstmeister/kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
```

Start:

```sh
docker-compose up -d
```

---

## **4️ Run Eureka, Config Server, Gateway, and Microservices**

From each service's directory:

```sh
mvn spring-boot:run
```

Or build JARs and run:

```sh
java -jar target/<service-name>.jar
```

---

##  Available APIs

### **Flight Service APIs**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/flight/search?from=HYD&to=DEL&date=2025-01-10` | Search flights |
| POST | `/api/flight/add` | Add new flight |

---

### **Booking Service APIs**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/flight/booking/create` | Book a ticket |
| GET | `/api/flight/booking/{id}` | Get booking details |

---

### **Email Service**
This service listens to Kafka topic:

```
ticket-booked
```

It sends confirmation emails after a successful booking.

---

##  Architecture Diagram (High-Level)

```
API Gateway → Booking Service → Flight Service
                               ↓
                       Kafka (ticket-booked)
                               ↓
                         Email Service

Config Server -----> All Microservices  
Eureka Server <----> All Microservices  
MySQL DBs <--------- Booking + Flight Services
```

---

##  Notes
- All services must register with Eureka.
- Config Server must start before other services.
- Kafka & DB must be running before Booking & Email services start.
- Email service requires:
  - Gmail App Password
  - Less secure apps disabled

---

##  Final Steps to Run Entire System

1. Start Kafka:  
   `docker-compose up -d`
2. Start Eureka  
3. Start Config Server  
4. Start Flight Service  
5. Start Booking Service  
6. Start Email Service  
7. Start API Gateway  

---

##  Project is ready to use!

---

