# 🌐 SEO Metadata Microservice (Spring Boot)

A modern, open-source Spring Boot microservice designed to **analyze SEO metadata** from any URL.  
This service uses **Jsoup**, **Hibernate**, and **Spring Security** to scrape, store, and expose SEO insights through a clean REST API.

---

## Tech Stack

- Spring Boot (REST API)
- Spring Security (JWT)
- Hibernate / JPA (MySQL)
- Jsoup (HTML parsing)
- (Optional later) Vaadin dashboard

---

# ✨ Features (Current & Planned)

### ✅ Implemented
- Spring Boot project setup
- MySQL database connection
- JPA/Hibernate schema generation
- MVC layered architecture (Model → Repository → Service → Controller)
- User & Role models
- Seed admin user via `CommandLineRunner`
- `/users` and `/roles` endpoints for testing

### 🛠️ In Progress
- Environment variable support (`.env`)
- Improved documentation (architecture, roadmap, API)

### 🔜 Upcoming
- JWT authentication (register + login)
- Complete SEO scraping module
- Extended metadata extraction
- DTO system replacing entity-based API
- Rate limiting (unauthenticated limited scans)
- Full analysis engine (SEO scoring)
- Optional Vaadin dashboard

---

# 🏗️ Architecture

This project uses a **feature-based MVC architecture**:

```
user/
 ├── User.java
 ├── Role.java
 ├── UserController.java
 ├── UserService.java
 ├── UserRepository.java
 └── RoleRepository.java

seo/          (future: scraping logic)
auth/         (future: JWT system)
scraping/     (future: Jsoup engine)
config/       (future: security config)
```

This structure is:

- scalable
- contributor-friendly
- aligned with modern Spring Boot practices
- easier to extend than classical layered architecture
- intentionally not “pure DDD” because JPA & security require framework annotations

For full architectural details, see:  
👉 **[ARCHITECTURE.md](./ARCHITECTURE.md)**

---

# 📦 Dependencies

## Key Maven dependencies used in this project:

### - Spring Boot:
- spring-boot-starter-web – REST API + embedded Tomcat
- spring-boot-starter-data-jpa – JPA + Hibernate integration
- spring-boot-starter-security – Spring Security core
- spring-boot-starter-validation – Bean validation (e.g. @Valid, @NotNull)
- spring-boot-devtools (runtime, optional) – Live reload during development
- spring-boot-starter-test (test scope) – Testing support (JUnit, etc.)
- spring-security-test (test scope) – Security-related testing utilities

### - Database & ORM:
- mysql-connector-j (runtime) – MySQL JDBC driver
- com.h2database:h2 (runtime) – In-memory database for local/dev testing

### - Security / JWT:
- io.jsonwebtoken:jjwt-api – JWT API
- io.jsonwebtoken:jjwt-impl (runtime) – JWT implementation

### - Utility:
- org.jsoup:jsoup – HTML parsing & scraping (for SEO metadata)
- org.projectlombok:lombok – Reduces boilerplate (getters/setters, constructors)
- me.paulschwarz:spring-dotenv – .env file support for environment variables

---

# 🚀 Getting Started

## 1. Clone the project
```bash
git clone https://github.com/yourusername/seo-metadata-microservice.git
```

## 2. Configure environment variables
Create a `.env` in the project root:

```
DB_URL=jdbc:mysql://host:3306/db
DB_USERNAME=yourusername
DB_PASSWORD=yourpassword
```

(Ensure `.env` is in `.gitignore`.)

## 3. Run the application
```bash
./mvnw spring-boot:run
```

---

# 📡 Development API

Current endpoints (temporary test endpoints):

- `GET /users`
- `POST /users`
- `GET /roles`
- `POST /roles`

For full documentation:  
👉 **[API.md](./API.md)**

---

# 🗺️ Roadmap

See:  
👉 **[ROADMAP.md](./ROADMAP.md)**

---

# 📄 License

**Apache 2.0**.
👉 **[LICENSE](./LICENSE)**

---

# 🤝 Contributing

PRs are welcome!  
👉 **[CONTRIBUTING.md](./CONTRIBUTING.md)**

--- 

# 👤 Author & Contact

**Thomas Heusdens**
Student at Erasmushogeschool Brussel (EHB) – Applied Computer Science / Software Engineering path

- 📧 Email: [thomasheusdens@gmail.com](mailto:thomasheusdens@gmail.com)
- 🔗 LinkedIn: [Thomas Heusdens](https://www.linkedin.com/in/thomas-heusdens-0bba19258/)

Feel free to reach out for collaboration, feedback, or contributions.

---

## Recourses
1. [Spring Boot - Database Integration (JPA, Hibernate, MySQL, H2)](https://www.geeksforgeeks.org/advance-java/spring-boot-database-integration-jpa-hibernate-mysql-h2/)
2. [Lombok Setup And Use](https://www.baeldung.com/lombok-ide)
3. [Lombok Getter and Setter](https://www.javabyexamples.com/delombok-getter-and-setter)
4. [Spring Boot - Annotations](https://www.geeksforgeeks.org/springboot/spring-boot-annotations/)
5. [Controller, Service, Repository](https://www.javaguides.net/2023/01/spring-boot-component-controller.html)