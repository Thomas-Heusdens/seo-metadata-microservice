# 📡 API Reference — SEO Metadata Microservice

This document lists the currently available API endpoints.  
These endpoints exist primarily for **internal testing** while the database, MVC structure, and services are being built.

Later, these will be replaced by:

- `/auth/login`
- `/auth/register`
- `/seo/scrape`
- `/seo/metadata`
- `/seo/analyze`

---

# 📌 Current Endpoints (Development Stage)

## 🟦 Users

### **GET /users**
Returns all users.

Example response:
```json
[
  {
    "id": 1,
    "email": "admin@test.com",
    "passwordHash": "test123",
    "roles": [
      {"id": 1, "name": "ROLE_USER"}
    ],
    "createdAt": "2025-11-17T15:10:00"
  }
]
```

---

### **GET /users/{id}**
Returns user by ID.

---

### **POST /users**
Creates a user (currently accepts full entity for testing purposes).

Example body:
```json
{
  "email": "test@test.com",
  "passwordHash": "12345"
}
```

---

## 🟩 Roles

### **GET /roles**
Returns all roles.

---

### **POST /roles**
Creates a role.

Example body:
```json
{
  "name": "ROLE_ADMIN"
}
```

---

# 📌 Authentication

⚠️ Not implemented yet.  
Final API will include:

- `POST /auth/register`
- `POST /auth/login`
- JWT-based authentication
- Role-based permissions

---

# 📌 SEO Endpoints

⚠️ Not implemented yet.  
Final API will include:

- `POST /seo/scrape`
- `GET /seo/metadata/{id}`
- `POST /seo/analyze`

---

# 📌 Notes

These endpoints are primarily for:

- validating database connection
- validating Hibernate schema generation
- verifying controllers and services
- testing with Postman

Official API endpoints will be added after authentication and DTO layers are implemented.