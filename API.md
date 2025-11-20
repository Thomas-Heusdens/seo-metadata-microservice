# 📡 API Reference — SEO Metadata Microservice

This API Reference describes the current endpoints of the **SEO Metadata Microservice**, including authentication, user/role management (admin only), and future SEO scraping endpoints.

The project is currently in **Version 0.2.0 — Authentication & Security**.

---

# 🔐 Authentication Endpoints (`/auth`)

These endpoints handle **user registration**, **login**, and **JWT token generation**.

---

## ✔ **POST /api/auth/register**

Creates a new user account.
Automatically assigns role: **ROLE_USER**.

**Request body:**

```json
{
  "username": "thomas",
  "password": "password123"
}
```

**Response:**

```json
{
  "id": 3,
  "username": "thomas",
  "roles": ["ROLE_USER"]
}
```

**Access:**
🟢 Public (no token required)

---

## ✔ **POST /api/auth/login**

Authenticates a user and returns a signed **JWT token**.

**Request body:**

```json
{
  "username": "admin",
  "password": "admin"
}
```

**Response:**

```json
{
  "username": "admin",
  "roles": ["ROLE_ADMIN"],
  "jwtToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Access:**
🟢 Public

Use the returned token for all protected endpoints:

```
Authorization: Bearer <jwt-token>
```

---

# 👥 Users API (`/users`)

These endpoints are **ADMIN-only** and currently exist primarily for internal testing.

---

## 🔐 **GET /users**

Returns all users.

**Access:**
🔴 Requires: `ROLE_ADMIN`

```java
@PreAuthorize("hasRole('ADMIN')")
```

---

## 🔐 **GET /users/{id}**

Returns a user by ID.

**Access:**
🔴 Requires: `ROLE_ADMIN`

---

## 🔐 **POST /users**

Creates a user **without validation** (temporary for testing).

**Access:**
🔴 Requires: `ROLE_ADMIN`

---

# 🏷 Roles API (`/roles`)

Internal-only testing endpoints for managing roles.

---

## 🔐 **GET /roles**

Returns all available roles.

**Access:**
🔴 Requires: `ROLE_ADMIN`
(You can decide later if you want this public.)

---

## 🔐 **POST /roles**

Creates a new role.

Example:

```json
{
  "name": "ROLE_MODERATOR"
}
```

**Access:**
🔴 Requires: `ROLE_ADMIN`

---

Absolutely — here is a **clean, corrected version** of the API Reference section, updated to reflect:

✔ Jsoup scraping **is now implemented**
✔ Metadata extraction endpoint exists
✔ No speculation about future endpoints
✔ No roadmap section (since it's in README)
✔ Only actual, working endpoints documented

---

# 🧪 scraper API

These endpoints allow authenticated users to extract metadata from any public webpage using **Jsoup**.

---

## 🟦 **POST /api/scraper/scrape**

Extracts metadata (title, description, favicon, and other `<meta>` tags) from a given URL.

**Request body:**

```json
{
  "url": "https://www.apple.com/"
}
```

**Example successful response:**

```json
{
  "url": "https://www.apple.com/",
  "title": "Apple",
  "description": "Discover the innovative world of Apple...",
  "favicon": "https://www.apple.com/favicon.ico",
  "metaTags": {
    "og:title": "Apple",
    "og:description": "Discover the innovative world of Apple",
    "twitter:card": "summary_large_image"
  }
}
```

**Access:**
🟣 Requires authentication

```java
@PreAuthorize("isAuthenticated()")
```