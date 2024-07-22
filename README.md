## Spring Security Integration: User Authentication, Registration, Email Verification, Forgot Password

### Overview

This repository provides a comprehensive Spring Boot application integrated with Spring Security to handle user authentication, registration, email verification, and password management. It serves as a foundational template for implementing secure and scalable user management features in a Java-based web application.

### Features

- **User Authentication**: Secure login and logout mechanisms using Spring Security. Supports both username/password and token-based authentication.
- **User Registration**: Endpoint for user registration. Handles storing user details securely and provides response with registration status.
- **Email Verification**: Automated email verification process to confirm user registration. Utilizes email service to send verification links and manage verification tokens.
- **Forgot Password**: Implements password reset functionality. Users can request a password reset email, and follow a secure link to update their password.
- **Secure Storage**: Passwords are hashed and salted using BCrypt for enhanced security.
- **Role-Based Access Control**: Configurable access control based on user roles (e.g., Admin, User).
- **JWT Integration**: JWT-based authentication for stateless security.

### Technologies Used

- **Spring Boot**: Framework for building the application.
- **Spring Security**: Security framework for authentication and authorization.
- **Spring Data JPA**: ORM framework for database interactions.
- **Hibernate**: ORM implementation.
- **JavaMailSender**: For sending email notifications.
- **BCrypt**: Password hashing mechanism.

### Getting Started

1. **Clone the Repository**:
   ```bash
   git clone  https://github.com/mustapha-essalih/Full-Featured-Spring-Security-Application.git
   cd spring-security-integration
   ```

2. **Configure Application**:
   - Set up your database connection in `application.properties` or `application.yml`.
   - Configure email settings for sending verification and password reset emails.

3. **Build and Run**:
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```
