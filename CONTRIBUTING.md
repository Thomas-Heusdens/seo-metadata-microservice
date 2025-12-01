# Contributing to the SEO Metadata Analyzer

Thank you for your interest in contributing!  
This project is part of the **Java Advanced course at Erasmushogeschool Brussel**, but external contributions are welcome as long as they follow the guidelines below.

---

## 📝 How to Contribute

### 1. **Report Issues**
Before creating a new issue, please:

- Check existing issues to avoid duplicates
- Provide a clear title and description
- Include logs, screenshots, or reproduction steps if relevant

### 2. **Submit Pull Requests**
Pull Requests (PRs) are welcome! Make sure to:

1. Fork the repository
2. Create a new branch from `main` (ex: `feature/vaadin-dashboard`, `fix/token-refresh`)
3. Commit using clear messages following _conventional commit_ guidelines:
    - `feat:` for new features
    - `fix:` for bug fixes
    - `docs:` for documentation updates
    - `refactor:` for code restructuring
4. Ensure your code builds and passes all tests
5. Submit a PR with a clear explanation of what was changed

---

## 🧩 Coding Standards

### Java (Spring Boot & Vaadin)
- Follow standard conventions (Google Java Style or IntelliJ auto-format)
- Keep classes small and focused (Single Responsibility Principle)
- Prefer constructor injection for Spring services
- Name endpoints consistently (`/api/scraper/...`, `/api/auth/...`)
- Do not commit secrets or environment variables

### Frontend (Vaadin Views)
- Keep UI logic separated from backend services
- Avoid placing business logic directly in View classes
- Use meaningful component IDs and names

---

## 🛠 Development Setup

### Requirements
- Java 25+
- Maven 3.9+
- MySQL

### Steps
1. Clone the repo
2. Configure environment variables (`application.properties` and `.env`)
3. Run: `mvn clean install` and `mvn spring-boot:run`

---

## 🔒 Security Notes

- Never commit credentials or tokens
- Report vulnerabilities privately
- Authentication flows must not be modified without review

---

## 🙌 Thank You!

Your contributions help improve the SEO metadata analyzer and support its evolution beyond the course project requirements.
