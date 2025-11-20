# 🗺️ **Project Roadmap — SEO Metadata Microservice**

This roadmap tracks major milestones completed across versions and highlights the current and upcoming development focus.

---

# ✅ **Version 0.1.0 — Initial Bootstrapping (COMPLETED)**

* Spring Boot setup
* MySQL connection & Hibernate/JPA configuration
* Automatic schema generation
* User & Role entities + repositories
* Basic service layer
* Testing controllers (`/users`, `/roles`)
* Initial architecture (MVC(S))
* Environment variables support (`.env`)
* Foundation documentation created (README, Architecture, API, Roadmap)

---

# ✅ **Version 0.2.0 — Authentication Layer (COMPLETED)**

### 🔐 **Security Core**

* Introduced role-based access control (RBAC)
* Implemented `CustomUserDetailsService`
* Secured endpoints using `@PreAuthorize`
* Added CORS configuration (`CorsConfig`)
* Introduced password hashing (BCrypt)

### 🔑 **JWT Authentication**

* Implemented JWT token generation & validation (`JwtUtils`)
* Added `AuthTokenFilter` for token extraction + authentication context
* Added `AuthEntryPointJwt` for 401/403 handling
* Implemented `/api/auth/login` endpoint
* Implemented `/api/auth/register` endpoint (default ROLE_USER)

### 🔒 **Security Result**

* API now fully supports secure login, registration, and token-based access
* All private endpoints require JWT authentication

---

# 🎯 **Version 0.3.0 — SEO Scraper (COMPLETED)**

### 🕸 **Jsoup Integration**

* Added `scraper` package containing Jsoup extraction logic
* Implemented core metadata extraction (title, description, meta tags, favicon)

### 🌐 **SEO Endpoint**

* Introduced `POST /api/scraper/extract` endpoint
* Protected by `@PreAuthorize("isAuthenticated()")`
* Returns structured metadata DTO

### 🔍 **Purpose**

* First working version of the scraping engine
* Baseline metadata extraction ready for future SEO analysis