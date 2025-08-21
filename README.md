# Client and Order Management API

A REST API for managing clients and orders built with Spring Boot and Java. This application supports client management, order processing, profit tracking, and comprehensive search functionality with business rule enforcement.

## Technology Stack

- **Programming Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL (production), H2 (development)
- **Build Tool**: Maven
- **Testing**: JUnit, Mockito
- **Deployment**: Docker with Docker Compose

## Features

### Client Management

- Client creation and registration
- Client information editing
- Client deactivation (soft delete)
- Client search by multiple criteria
- Profit tracking and reporting

### Order Management

- Order creation with business validation
- Order processing with simulated delays
- Profit calculation and enforcement
- Unique business key constraints

### Search & Analytics

- Client search by name, email, or address
- Profit range filtering
- Order history tracking
- Concurrent operation handling

## API Endpoints

### Client Endpoints

- `GET /api/clients` - Get all clients
- `GET /api/clients/{id}` - Get client by ID
- `POST /api/clients` - Create a new client
- `PUT /api/clients/{id}` - Update client information
- `DELETE /api/clients/{id}` - Deactivate a client
- `GET /api/clients/search` - Search clients by criteria
- `GET /api/clients/{clientId}/profit` - Get client's total profit
- `GET /api/clients/by-profit` - Filter clients by profit range

### Order Endpoints

- `GET /api/orders` - Get all orders
- `GET /api/orders/{id}` - Get order by ID
- `POST /api/orders` - Create a new order
- `GET /api/orders/clients/{clientId}/orders` - Get orders for a specific client

## Setting Up the Project

### Prerequisites

- JDK 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for containerized deployment)

### Running with Docker

1. Clone the repository
   ```bash
   git clone [<repository-url>](https://github.com/marynakozelska/dataox_test.git)
   cd dataox_test

2. Build and start the services using Docker Compose
    ``` bash
   docker-compose up --build
3. The API will be available at http://localhost:8080
4. Adminer (database management) will be available at http://localhost:8081
5. Swagger UI will be available at http://localhost:8080/swagger-ui/index.html#/

### Running Tests
- Run the tests using Maven:
    ``` bash
    mvn test
# Error Handling

The API provides comprehensive error handling with appropriate HTTP status codes:

- **400 Bad Request** for validation errors
- **404 Not Found** for missing resources
- **409 Conflict** for business rule violations
- **500 Internal Server Error** for server issues
