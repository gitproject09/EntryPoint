# **_EntryPoint Project_**

Overview
---------
The EntryPoint Project is an authentication service built using Spring Boot that provides a set of APIs for user registration, login, OTP verification, profile management, and more. 
This project is designed to authenticate users using JWT tokens, with OTPs stored in Redis for caching and PostgreSQL used as the primary database for user data storage. 
The project also includes Flyway migrations for database version control.


Technologies Used
-----------------
- Spring Boot: Framework for building the application.
- PostgreSQL: Database for storing user information.
- Spring Data JPA: For database interactions and ORM.
- Lombok: To reduce boilerplate code like getters, setters, and constructors.
- Spring Security: For securing endpoints and JWT authentication.
- Spring Web: For creating REST APIs.
- Spring Data Redis: For caching OTP data.
- Flyway Migration: For handling database migrations.
- JWT: For authentication and generating tokens.
- Redis: For caching OTPs during the registration and login process.

Features
--------
- User Registration: Allows users to register by providing an email and password.
- OTP Verification: Users can verify their email via OTP sent to their email address.
- Login: Users can log in with their credentials and receive a JWT token.
- Get Profile: Authenticated users can retrieve their profile information.
- Refresh Token: Users can refresh their JWT token to stay authenticated.
- Logout: Users can log out, which invalidates their JWT token.

API Endpoints
-------------
1. Register
- URL: /api/v1/auth/register
- Method: POST
- Request Body:
  {
  "email": "user@example.com",
  "password": "password123"
  }
- Description: Registers a new user. An OTP is generated and sent to the user's email for verification.

3. Verify OTP
- URL: /api/v1/auth/verify-otp
- Method: POST
- Params:
  "email": "user@example.com",
  "otp": "123456"

- Description: Verifies the OTP sent to the user's email during registration.

3. Login
- URL: /api/v1/auth/login
- Method: POST
- Request Body:
  {
  "email": "user@example.com",
  "password": "password123"
  }
- Description: Authenticates the user and returns a JWT token.

4. Get Profile
- URL: /api/v1/auth/get-profile
- Method: GET
- Description: Fetches the profile details of the authenticated user.

5. Refresh Token
- URL: /api/v1/auth/refresh
- Method: POST
- Description: Refreshes the JWT token using a valid refresh token.

6. Logout
- URL: /api/v1/auth/logout
- Method: POST
- Description: Logs out the user by invalidating the JWT token.

Installation
------------
Prerequisites:
- Java 17
- PostgreSQL database
- Redis for caching OTPs
- Gradle Groovy for dependency management

Steps:
1. Clone the repository:
   git clone https://github.com/toukir99/entrypoint.git
   cd entrypoint

2. Set up your application.properties (in build.gradle):
   spring.datasource.url=jdbc:postgresql://localhost:5432/yourdb
   spring.datasource.username=yourusername
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   spring.flyway.enabled=true
   spring.redis.host=localhost
   spring.redis.port=6379
   jwt.secret=your_jwt_secret

3. Run Database Migrations:
- Flyway will automatically run database migrations on application startup if spring.flyway.enabled=true is set.

4. Build and Run the Application:
 using Gradle:
./gradlew bootRun

5. The application will start on http://localhost:8080 (default).

Testing
-------
You can use tools like Postman to test the API endpoints.

Security
--------
The API uses JWT (JSON Web Token) for authentication. 
After a successful login, a JWT token is returned and must be included in the Authorization header as a Bearer token for all protected endpoints.

Example:
Authorization: Bearer <your-jwt-token>

Future Improvements
-------------------
- Add email verification before allowing login.
- Implement password recovery functionality.
- Add role-based access control (RBAC) for authorization.

By following the steps above, you can set up and run the EntryPoint Project locally and interact with its authentication APIs.
