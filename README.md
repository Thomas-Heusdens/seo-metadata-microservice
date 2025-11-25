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
- role based access to endpoints using accounts from database (test)
- JWT authentication (register + login)
- Complete SEO scraping module using Jsoup
- Extended metadata extraction
- Analysis of the extracted metadata (logic)
- Refresh and access tokens (logout and refresh endpoint)

### 🛠️ In Progress
- Vaadin and OAuth 2.0 (login) integration

### 🔜 Upcoming
- Compare two URLs side-by-side
- Full analysis engine (SEO scoring)

---

# 🏗️ Architecture

This project uses a **feature-based MVC architecture**:

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
git clone https://github.com/thomas-heusdens/seo-metadata-microservice.git
```

## 2. Configure environment variables
Create a `.env` in the project root:

```
DB_URL=jdbc:mysql://host:3306/db
DB_USERNAME=yourusername
DB_PASSWORD=yourpassword
JWT_SECRET_KEY=yourverylongsecretkey
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
- `get /users/{id}`
- `GET /roles`
- `POST /roles`
- `POST /api/auth/signin`
- `POST /api/auth/register`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `GET /api/scraper/extract`
- `GET /api/scraper/analyze`

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
6. [Spring Security Basics (whole playlist)](https://www.youtube.com/watch?v=RabQl8XNt3s&list=PLxhSr_SLdXGOpdX60nHze41CvExvBOn09&index=1)
7. [PasswordEncoder](https://www.geeksforgeeks.org/advance-java/spring-security-preauthorize-annotation-for-method-security/)
8. [JWT GitHub Repository](https://github.com/jwtk/jjwt?tab=readme-ov-file#maven)
9. [CORS::Spring Framework](https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html)
10. [Web Scrapping in Java with Jsoup (Basics)](https://www.youtube.com/watch?v=riZ2GAaMDGM)
11. [Jsoup official documentation](https://jsoup.org/cookbook/input/load-document-from-url)
12. [How access and refresh tokens work](https://www.youtube.com/watch?v=VVn9OG9nfH0&t=4520s)
13. [JSON decoding](https://developer.mozilla.org/en-US/docs/Web/API/Window/atob)
13. [Vaadin basics](https://www.youtube.com/watch?v=67oJxPRa3Mg)
14. [Vaadin advanced topics](https://vaadin.com/docs/latest/flow)