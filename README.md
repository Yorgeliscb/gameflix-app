GameFlix --

Technologies
Java 21, Spring Boot 4.1.1, Spring Security, 
Spring Data JPA, MySQL, Maven, and Postman

API Endpoints
* POST /api/auth/register — Registers a new user (201, 400, 409).
* POST /api/auth/login — Authenticates users and returns a JWT (200, 401)
* GET /api/users/me — Returns the authenticated user's profile (200, 401)


Database Setup
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100)
);

Security
Passwords are being hashed using BCrypt with strength 12.
It provides a stronger protection than strength 10.

Module 3 Design Changes
For Module 4, I focused on implementing the User table for registration and login.
I changed userID to an auto-incrementing BIGINT id, seperated name into firstName and lastName.
Also added a unique constraint to email to prevent duplicate accounts.

AI Assistance
I used ChatGPT to help write Java classes, and configure Spring Security. 
I changed its initial Spring Boot 3.5 recommendation to Spring Boot 4.1.1.

