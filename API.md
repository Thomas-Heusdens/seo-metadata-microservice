# ✅ **UPDATED API REFERENCE (ready to paste)**

Includes the fully implemented scraping endpoint + cleaned structure.

---

# 📡 API Reference — SEO Metadata Microservice

This API Reference documents the currently available endpoints of the **SEO Metadata Microservice**, including authentication, user/role management, and SEO metadata extraction.

The project is currently in:

**Version 1.0.0 — Stable Release (Authentication + Scraper Prototype)**

---

# 🔐 Authentication Endpoints (`/api/auth`)

These endpoints handle registration, login, and JWT token generation.

---

## ✔ **POST /api/auth/register**

Creates a new user account.
Default role: **ROLE_USER**

### Request

```json
{
  "username": "thomas",
  "password": "password123"
}
```

### Response

```json
{
  "id": 3,
  "username": "thomas",
  "roles": ["ROLE_USER"]
}
```

**Access:** Public

---

## ✔ **POST /api/auth/login**

Authenticates the user and returns a signed JWT token.

### Request

```json
{
  "username": "admin",
  "password": "admin"
}
```

### Response

```json
{
  "username": "admin",
  "roles": ["ROLE_ADMIN"],
  "jwtToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Access:** Public

---

## ✔ **POST /api/auth/logout**

Logs the user out using the HttpCookie refresh token.

### Response

```json
{
  "message": "Logged out successfully"
}
```

**Access:** Protected

Use the token for all protected endpoints:
`Authorization: Bearer <token>`

---

## ✔ **POST /api/auth/refresh**

Refreshes the access token and rotates the refresh token.

### Request

```json
{
  "refreshToken": "<REFRESH_TOKEN>"
}
```

### Response

```json
{
  "accessToken": "<ACCESS_TOKEN>",
  "refreshToken": "<REFRESH_TOKEN>",
  "tokenType": "Bearer"
}
```

**Access:** Public

---

# 👥 Users API (`/users`)

Admin-only internal endpoints.

---

## 🔐 **GET /users**

Returns all users.
**Access:** Requires `ROLE_ADMIN`

---

## 🔐 **GET /users/{id}`**

Returns a user by ID.
**Access:** Requires `ROLE_ADMIN`

---

## 🔐 **POST /users**

Creates a new user without validation.
**Access:** Requires `ROLE_ADMIN`

---

# 🏷 Roles API (`/roles`)

Admin-only endpoints for managing roles.

---

## 🔐 **GET /roles**

Returns all roles.
**Access:** Requires `ROLE_ADMIN`

---

## 🔐 **POST /roles**

Creates a new role.

### Request

```json
{
  "name": "ROLE_MODERATOR"
}
```

**Access:** Requires `ROLE_ADMIN`

---

# 🧪 Scraper API (`/api/scraper`)

Endpoints for extracting metadata from any public webpage.

---

## 🟦 **GET /api/scraper/extract**

Extracts SEO metadata using Jsoup based on a URL.

### Example Request:

```
GET /api/scraper/extract?url=https://skwd.be
```

### Example Response (shortened):

```json
{
  "url": "https://skwd.be",
  "title": "SKWD",
  "description": "Student staffing agency...",
  "canonicalUrl": "https://skwd.be",
  "ogTitle": "SKWD – Student Staffing",
  "h1": "Welcome to SKWD",
  "h2List": ["Staffing Agency", "Our Services"],
  "robots": "index,follow",
  "viewport": "width=device-width, initial-scale=1",
  "jsonLdList": ["{...}"],
  "internalLinksCount": 42,
  "externalLinksCount": 10,
  "missingAltCount": 3,
  "wordCount": 1020
}
```

**Access:**
🔒 Requires authentication

```java
@PreAuthorize("isAuthenticated()")
```

---

# 🧪 SEO API (`/api/seo`)

Endpoints for extracting metadata from any public webpage.

---

## 🟦 **GET /api/scraper/analyze**

Uses the api/scraper/extract api and applies the logic on the retrieved content to give feedback.

### Example Request:

```
GET /api/seo/analyze?url=https://skwd.be
```

### Example Response (shortened):

```json
{

}
```

**Access:**
🔒 Requires authentication

```java
@PreAuthorize("isAuthenticated()")
```