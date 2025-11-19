# 🗺️ Project Roadmap — SEO Metadata Microservice

This roadmap tracks major milestones achieved during development.

---

# ✅ Version 0.1.0 — Initial Bootstrapping (COMPLETED)

- Spring Boot project setup
- MySQL database connection
- Hibernate/JPA configuration
- Automatic schema generation
- User & Role entities
- User and Role repositories
- Service layer implementation
- REST controllers (`/users`, `/roles`)
- CommandLineRunner seeding default admin
- MVC(S) architecture established
- Environment variables support added (`.env`)
- Documentation foundation (README, ARCHITECTURE, API, Roadmap)

---

# 🎯 Version 0.2.0 — Authentication Layer (IN PROGRESS)
## 🔐 Security Foundation (Completed)
- Introduced role-based access control (RBAC) using:
    - CustomUserDetailsService
    - SecurityConfiguration
    - Role-based @PreAuthorize API protection
    - Integrated .env variable loading with strong JWT secret key
    - Added proper password hashing with BCrypt
    - Locked down endpoints based on user roles
## 🔑 JWT Authentication & Authorization (Completed)
- Implemented JWT token generation (JwtUtils)
- Implemented JWT validation with expiration logic
- Added AuthTokenFilter to inject authentication context
- Added AuthEntryPointJwt for 401/403 handling
- Built /signin endpoint using LoginRequest & LoginResponse DTOs
- Applied token-based authentication to protected routes
- Successfully tested ADMIN/USER access using:
    - invalid token
    - valid token
    - expired token
    - wrong role token
## 🧱 Next Steps for v0.2.0
- Implement user registration (/register)
    - Validation (unique username)
    - Hashing passwords
    - Default role assignment (e.g., USER)
    - Return DTOs after register
- Implement JWT logout
    - (invalidate token by adding blacklist or short TTL)