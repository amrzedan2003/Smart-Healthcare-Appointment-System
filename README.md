# Smart Healthcare Appointment System

A web-based healthcare management platform for appointment scheduling, patient management, and medical records.

## Features

- **User Management**: Secure authentication with role-based access (Admin, Doctor, Patient)
- **Appointment Scheduling**: Real-time booking and management
- **Doctor Management**: Profile and availability management
- **Patient Records**: Secure patient information tracking
- **Prescription Management**: Digital prescription creation
- **Working Time Slots**: Flexible doctor schedule management

## Technology Stack

- **Spring Boot 3.5.5** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database layer
- **PostgreSQL** - Primary database
- **MongoDB** - Document storage
- **JWT** - Authentication tokens
- **Maven** - Build tool
- **JUnit 5** - Testing framework

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.6+
- PostgreSQL 12+
- MongoDB 5+
- Git

### Docker Setup

The easiest way to run the application is using Docker:

#### Prerequisites for Docker

- Docker
- Docker Compose

#### Quick Start with Docker

1. **Clone the repository**

   ```bash
   git clone https://github.com/amrzedan2003/Smart-Healthcare-Appointment-System.git
   cd Smart-Healthcare-Appointment-System
   ```

2. **Start all services**

   ```bash
   docker-compose up -d
   ```

   This will:

   - Build the Spring Boot application
   - Start PostgreSQL database
   - Start MongoDB database
   - Start the healthcare application

3. **Access the application**

   Open: `http://localhost:8080`

#### Docker Commands

- **Start all services**: `docker-compose up -d`
- **Stop all services**: `docker-compose down`
- **View logs**: `docker-compose logs -f backend`
- **Rebuild and start**: `docker-compose up --build -d`
- **Remove volumes (fresh start)**: `docker-compose down -v`

## Docker Architecture

The application uses the following Docker services:

- **backend**: Spring Boot application (Port 8080)
- **postgres**: PostgreSQL database (Port 5432)
- **mongodb**: MongoDB database (Port 27017)

All services communicate through a custom Docker network called `healthcare-network`.

## Environment Variables

The application uses the following environment variables (defined in `.env`):

- `POSTGRES_DB`: PostgreSQL database name
- `POSTGRES_USER`: PostgreSQL username
- `POSTGRES_PASSWORD`: PostgreSQL password
- `MONGODB_DATABASE`: MongoDB database name
- `JWT_SECRET`: JWT token secret key
- `JWT_EXPIRATION`: JWT token expiration time
- `SERVER_PORT`: Application server port

## API Endpoints

### Authentication

- `POST /api/auth/login` - User login

### Core APIs

- `GET/POST /api/patients` - Patient management
- `GET/POST /api/doctors` - Doctor management
- `GET/POST /api/appointments` - Appointment management
- `GET/POST /api/prescriptions` - Prescription management

### Example Request

```json
POST /api/auth/login
{
  "email": "amr.zedan@admin.com",
  "password": "amr123456"
}
```

## Testing

### Run All Tests

```bash
mvn test
```

## Project Structure

```
src/main/java/ps/exalt/healthcare_appointment_system/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── repository/      # Data repositories
├── service/         # Business logic
└── exception/       # Custom exceptions
```

## Security

- JWT token authentication
- Role-based access control (Admin, Doctor, Patient)
- BCrypt password encryption
- Input validation and SQL injection prevention
