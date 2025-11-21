# 🗺️ Project Roadmap — SEO Metadata Microservice

This roadmap outlines the major progression of the project and the development focus for upcoming versions.

---

# ✅ **Version 1.0.0 — Stable Prototype Release (NEW)**

*(Wraps all versions 0.1.0 → 0.4.0)*

### 🔧 Contains everything from previous milestones:

#### 🔹 Bootstrapping

* Spring Boot + Maven setup
* MySQL + Hibernate/JPA
* MVC architecture
* Entities + Repositories
* Environment variable support

#### 🔹 Authentication System

* JWT login + registration
* BCrypt password encryption
* Role-based access control
* Security filters + exception handler
* Admin-only user & role management endpoints

#### 🔹 Scraper Prototype

* Full Jsoup integration
* Complete metadata extraction service
* JSON-LD support
* Image/alt scanning
* Word count analysis
* H1/H2 structure extraction
* Robots, viewport, Apple touch icon
* Twitter + Open Graph metadata
* `/api/scraper/extract` and `/api/scraper/analyze` endpoint

### 🎉 Result:

**Fully working secured API + first complete SEO metadata extraction engine.**

---

# 🎯 **Version 2.0.0 — Online Presence Comparison Engine (UPCOMING)**

### 🔜 Planned features:

* Compare **two URLs side-by-side**
* Determine strengths/weaknesses (your site vs competitor)
* Provide “SEO coaching tips” using structured rules
* Score metadata quality (0–100) -> not sure
* Detect missing critical tags vs reference
* Return comparison DTO
* Add Vaadin dashboard UI

### 🔐 Security Upgrade:

* Introduce **OAuth 2.0 / OpenID Connect** login option
* Preserve JWT support for API clients

### 🎛 UI Upgrade:

* Vaadin admin interface
* Dashboard for viewing scraping results
* Compare-mode visualizer

---

# 🚀 **Version 3.0.0 — Full SaaS Platform (PLANNED)**

### Long-term vision:

- Coming soon