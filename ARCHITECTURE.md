# 🏗️ Architecture Overview — SEO Metadata Microservice

This document describes the architecture, design decisions, and internal structure of the **SEO Metadata Microservice** built with **Spring Boot**, **Spring Security**, **Hibernate/JPA**, and **Jsoup**.

The goal of this project is to build an open-source, scalable, developer-friendly microservice that can authenticate users, scrape SEO metadata from any URL, and store detailed results for later analysis.

---

# 📌 High-Level Architecture

The project follows a **feature-based MVC(S)** structure:

```
seo-metadata-microservice/
 ├── user/
 │    ├── User.java                    # User entity with roles + password hashing
 │    ├── Role.java                    # Role entity (USER / ADMIN)
 │    ├── UserService.java             # Business logic for users
 │    ├── RoleService.java             # Business logic for roles
 │    ├── UserRepository.java          # JPA repo for User
 │    ├── RoleRepository.java          # JPA repo for Role
 │    ├── UserController.java          # REST API for fetching users
 │    └── RoleController.java          # REST API for managing roles
 │
 ├── security/
 │    ├── SecurityConfiguration.java     # Spring Security config, JWT filter, RBAC, CORS enabled
 │    ├── CustomUserDetailsService.java  # Loads user details + roles from DB for authentication
 │    ├── CorsConfig.java                # Defines allowed origins, headers, and JWT access for the frontend
 │    └── PasswordConfig.java            # Contains the PasswordEncoder Bean
 │
 ├── auth/
 │    ├── refresh/
 │         ├── RefreshToken.java                  # RefreshToken entity
 │         ├── RefreshTokenRepository.java        # JPA repo for RefreshToken
 │         ├── RefreshTokenService.java           # Business logic for Refresh Tokens
 │         ├── TokenRefreshException.java         # Exception handling of Refresh Tokens
 │         ├── TokenRefreshRequest.java           # DTO: Refresh token
 │         └── TokenRefreshResponse.java          # DTO: Refresh token, access token and token type
 │    ├── oauth2/
 │         ├── CustomOAuth2UserService.java       # Handles new and already existing users connecting with Google
 │         ├── OAuth2Config.java                  # JPA repo for RefreshToken
 │    ├── AuthController.java            # Handles /api/auth/signin and /api/auth/register
 │    ├── LoginRequest.java              # DTO: login username + password
 │    ├── LoginResponse.java             # DTO: username, roles, JWT token
 │    ├── RegisterRequest.java           # DTO: registration username + password
 │    ├── LogoutRequest.java             # DTO: refresh token and logoutAll boolean
 │    ├── MessageResponse.java           # DTO: Succesful message for logout
 │    └── RegisterResponse.java          # DTO: server response after user registration
 │
 ├── jwt/
 │    ├── JwtUtils.java                  # generate/validate tokens
 │    ├── AuthTokenFilter.java           # extracts JWT from headers
 │    └── AuthEntryPointJwt.java         # handles unauthorized errors
 │
 ├── seo/                           
 │    ├── SeoAnalysisResult.java         # DTO for analysis result
 │    ├── SeoAnalysisService.java        # Logic to analyse extracted metadata
 │    ├── SeoCheck.java                  # DTO for the check of each metadata element
 │    └── SeoController.java             # Handles /api/scraper/analyze
 │                        
 ├── scraping/                           
 │    ├── ScrapingController.java        # Handles /api/scraper/extract
 │    ├── ScrapingMetadata.java          # DTO for extracting metadata
 │    └── ScrapingService.java           # Logic to extract metadata using Jsoup
 ├── gui/
 │    ├── components/
 │         └── LogoutButton.java         # Contains logic and styling for logout button
 │    ├── Config.java                    # Loads a js module
 │    ├── HomeView.java                  # Contains logic and styling of homepage
 │    ├── LoginView.java                 # Contains logic and styling of login form
 │    ├── MainLayout.java                # Contains the navigation
 │    ├── RegisterView.java              # Contains logic and styling of register form
 │    ├── OAuthSuccessView.java          # Redirects the user to the homepage after a successful login with Google or Github
 │    └── SeoAnalysisView.java           # Displays the result of the fetch to api/seo/analyze
 │
 ├── SeoMetadataMicroserviceApplication.java
 ├── application.properties
 ├── auth-interceptor.js                 # Contains logic of the refresh and access token linked with backend flow
 ├── .env
 └── documentation (README, API, ROADMAP, ARCHITECTURE, LICENSE, ANALYSIS)
```

This approach organizes the code **per feature/domain**, not per technical layer, which is more scalable and easier for contributors to understand.

---

# 📌 Why MVC(S) instead of strict Domain-Driven Design?

Although DDD concepts influenced the design, this microservice relies heavily on:

- Spring Boot
- JPA / Hibernate
- Spring Security
- Spring Data Repositories
- REST Controllers

A *pure* DDD architecture requires:

- domain objects with **no annotations**
- domain repositories defined as **interfaces only**
- infrastructure adapters converting domain ↔ persistence objects
- completely framework-free domain layer

This is counter-productive for a microservice that:

- is centered around JPA entities
- requires annotations like `@Entity`, `@Id`, `@Table`
- uses Spring controllers and security heavily
- is meant to be lightweight and maintainable

Therefore, we follow a **pragmatic architecture**:

### ✔ MVC(S) structure (Model → Repository → Service → Controller)
### ✔ Feature-based packages (better than layered packages)
### ✔ “DDD-light” concepts where useful
### ✔ Domain logic inside entities and services
### ✔ Clean separation between service and controller
### ✔ DTOs coming later (when auth & SEO endpoints stabilize)

This is the **correct architecture** for a Spring Boot microservice that needs to stay simple, open-source, and framework-integrated.

---

# 📌 Components Explained

## **1. Entities (Model Layer)**
Located in: `user/`

- `User`
- `Role`
- `RefreshToken`

Each entity is mapped using JPA annotations:

```java
@Entity
@Table(name = "users")
```

These models represent the database structure and hold the core data of the system.

---

## **2. Repositories (Data Access Layer)**

`UserRepository`, `RoleRepository` and `RefreshTokenRepository` extend `JpaRepository`, giving:

- `.findById()`
- `.findAll()`
- `.save()`
- `.existsByEmail()`
- `.findByToken`
- `.countByUser`
- custom queries

Spring Data JPA automatically implements these interfaces.

---

## **3. Services (Business Logic Layer)**

`UserService`, `RoleService`, `RefreshTokenService`, `ScraperService`, and `SeoAnalysisService` contain:

- business logic
- validation
- interactions with repositories
- orchestration for controller → DB flow

This keeps controllers thin and maintainable.

---

## **4. Controllers (REST API Layer)**

`UserController`, `RoleController`, `AuthController`, `SeoController` and `ScraperController` expose REST endpoints:

- `GET /users`
- `POST /users`
- `get /users/{id}`
- `GET /roles`
- `POST /roles`
- `POST /api/auth/signin`
- `POST /api/auth/register`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `GET /api/scraper/extract`
- `GET /api/seo/analyze`
---

## **5. Configuration (Spring Boot / Security)**

Includes:

- JWT token provider
- Authentication filters
- WebSecurityConfig
- Password encoders
- CORS settings

---

## **6. SEO Scraping (Jsoup)**

Includes:

- fetching HTML from target URLs
- extracting titles, meta descriptions, canonical URLs
- extracting OpenGraph metadata
- extracting JSON-LD structures
- hreflang detection
- returning structured DTO responses