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

- Java 24+
- Maven 3.6+
- PostgreSQL 12+
- Git

### Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/amrzedan2003/Smart-Healthcare-Appointment-System.git
   cd Smart-Healthcare-Appointment-System
   ```

2. **Setup Database**

   ```sql
   CREATE DATABASE healthcare_db;
   CREATE USER postgres WITH PASSWORD '123456';
   GRANT ALL PRIVILEGES ON DATABASE healthcare_db TO postgres;
   ```

3. **Update Configuration**

   Edit `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/healthcare_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Run Application**

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access Application**

   Open: `http://localhost:8080`

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
