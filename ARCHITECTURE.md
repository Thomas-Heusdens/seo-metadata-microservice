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
 │    └── CorsConfig.java                # Defines allowed origins, headers, and JWT access for the frontend
 │
 ├── auth/
 │    ├── AuthController.java            # Handles /api/auth/signin and /api/auth/register
 │    ├── LoginRequest.java              # DTO: login username + password
 │    ├── LoginResponse.java             # DTO: username, roles, JWT token
 │    ├── RegisterRequest.java           # DTO: registration username + password
 │    └── RegisterResponse.java          # DTO: server response after user registration
 │
 ├── jwt/
 │    ├── JwtUtils.java                  # generate/validate tokens
 │    ├── AuthTokenFilter.java           # extracts JWT from headers
 │    └── AuthEntryPointJwt.java         # handles unauthorized errors
 │
 ├── seo/                                # (future) HTML parsing logic
 ├── scraping/                           # (future) metadata extraction engine
 │
 ├── SeoMetadataMicroserviceApplication.java
 ├── application.properties
 ├── .env
 └── documentation (README, API, ROADMAP, ARCHITECTURE, LICENSE)
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

Each entity is mapped using JPA annotations:

```java
@Entity
@Table(name = "users")
```

These models represent the database structure and hold the core data of the system.

---

## **2. Repositories (Data Access Layer)**

`UserRepository` and `RoleRepository` extend `JpaRepository`, giving:

- `.findById()`
- `.findAll()`
- `.save()`
- `.existsByEmail()`
- custom queries

Spring Data JPA automatically implements these interfaces.

---

## **3. Services (Business Logic Layer)**

`UserService` and `RoleService` contain:

- business logic
- validation
- interactions with repositories
- orchestration for controller → DB flow

This keeps controllers thin and maintainable.

---

## **4. Controllers (REST API Layer)**

`UserController`, `RoleController`, and `LoginController` expose REST endpoints:

- `GET /users`
- `POST /users`
- `GET /roles`
- `POST /roles`
- `POST /signin`
- `POST /register`

At this stage, these controllers serve as **testing endpoints** for:

✔ verifying DB connection  
✔ verifying Hibernate schema generation  
✔ verifying repositories and services  
✔ verifying Spring Web MVC

These endpoints will later be replaced with:

- `/auth/register`
- `/auth/login`
- `/seo/scrape`
- `/seo/metadata/{id}`

---

## **5. Configuration (Spring Boot / Security)**

Include:

- JWT token provider
- Authentication filters
- WebSecurityConfig
- Password encoders
- CORS settings (Future)

---

## **6. SEO Scraping (Jsoup)**

Later features:

- fetching HTML from target URLs
- extracting titles, meta descriptions, canonical URLs
- extracting OpenGraph metadata
- extracting JSON-LD structures
- hreflang detection
- storing SEO results in DB
- returning structured DTO responses

---

# 📌 Data Flow Diagram

```
               ┌────────────────────────┐
HTTP Request → │  AuthTokenFilter (JWT) │
               └────────────────────────┘
                           │
                           ▼
               ┌────────────────────────┐
               │ SecurityContextHolder   │
               │ (Authentication + Roles)│
               └────────────────────────┘
                           │
                           ▼
                    Authorization?
                (PreAuthorize, RBAC check)
                           │
         if ok ────────────┘
                           ▼
                  ┌────────────────┐
                  │   Controller   │
                  └────────────────┘
                           │
                           ▼
        (DTO mapping)  →  Service Layer
                           │
                           ▼
                     Repository Layer
                           │
                           ▼
                        MySQL DB
```
