# 🏗️ Architecture Overview — SEO Metadata Microservice

This document describes the architecture, design decisions, and internal structure of the **SEO Metadata Microservice** built with **Spring Boot**, **Spring Security**, **Hibernate/JPA**, and **Jsoup**.

The goal of this project is to build an open-source, scalable, developer-friendly microservice that can authenticate users, scrape SEO metadata from any URL, and store detailed results for later analysis.

---

# 📌 High-Level Architecture

The project follows a **feature-based MVC(S)** structure:

```
seo-metadata-microservice/
 ├── user/
 │    ├── User.java
 │    ├── Role.java
 │    ├── UserService.java
 │    ├── RoleService.java
 │    ├── UserRepository.java
 │    ├── RoleRepository.java
 │    ├── UserController.java
 │    └── RoleController.java
 │
 ├── config/        (later: security, CORS, JWT)
 ├── seo/           (later: SEO extraction, Jsoup logic)
 ├── scraping/      (later: metadata scraping engine)
 ├── auth/          (later: authentication + JWT)
 └── application root files
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

`UserController` and `RoleController` expose REST endpoints:

- `GET /users`
- `POST /users`
- `GET /roles`
- `POST /roles`

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

Will include later:

- JWT token provider
- Authentication filters
- WebSecurityConfig
- Password encoders
- CORS settings

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
HTTP Request
     │
     ▼
Controller  ← DTO (later)
     │
     ▼
Service (business logic)
     │
     ▼
Repository (Spring Data JPA)
     │
     ▼
Database (MySQL)
```

---

# 📌 Summary

This architecture is:

- simple
- scalable
- open-source friendly
- Spring Boot idiomatic
- compatible with JPA & Security requirements
- easy for contributors
- ready for future DDD-light improvements

A full DDD/Hexagonal approach is intentionally avoided to keep the microservice lightweight and aligned with its purpose.

