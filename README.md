# PeerHub Backend — Spring Boot

A Spring Boot REST API backend for the PeerHub peer-review platform.

## Tech Stack
- Java 17, Spring Boot 3.2.4
- Spring Security (JWT stateless auth)
- Spring Data JPA + MySQL
- BCrypt password hashing

---

## Running Locally (Eclipse)

### Prerequisites
- Java 17+
- Maven (or use `./mvnw`)
- MySQL running on port 3306

### Steps
1. Open Eclipse → **File → Import → Existing Maven Projects** → point to this folder.
2. MySQL auto-creates the `peerhub_db` database on first run.
3. Run `PeerHubApplication.java` as a Java Application.
4. API is available at `http://localhost:8080/api`

### Default credentials (seeded)
| Role       | Email                         | Password     |
|------------|-------------------------------|--------------|
| Student    | alex@university.edu           | student123   |
| Student    | priya@university.edu          | student123   |
| Instructor | prof.rivera@university.edu    | teach123     |

---

## API Endpoints

| Method | Path                  | Auth       | Description              |
|--------|-----------------------|------------|--------------------------|
| POST   | /api/auth/login       | Public     | Login → returns JWT      |
| GET    | /api/auth/me          | Any auth   | Current user info        |
| GET    | /api/health           | Public     | Health check             |
| GET    | /api/projects         | Any auth   | List all projects        |
| GET    | /api/projects/{id}    | Any auth   | Get project by ID        |
| GET    | /api/reviews          | Any auth   | All submitted reviews    |
| GET    | /api/reviews/pending  | Any auth   | Pending reviews          |
| POST   | /api/reviews          | Any auth   | Submit a review          |
| GET    | /api/students         | INSTRUCTOR | List all students        |
| GET    | /api/assignments      | Any auth   | List all assignments     |
| POST   | /api/assignments      | INSTRUCTOR | Create assignment        |
| GET    | /api/settings         | Any auth   | Get course settings      |
| PUT    | /api/settings         | INSTRUCTOR | Update course settings   |

---

## Deploying to Render

1. Push this `backend` folder to a GitHub repository.
2. On Render → **New Web Service** → connect the repo.
3. Set these environment variables in the Render dashboard:

| Variable       | Value                                                                                      |
|----------------|--------------------------------------------------------------------------------------------|
| DATABASE_URL   | `jdbc:mysql://<host>:3306/peerhub_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
| DB_USER        | your MySQL username                                                                        |
| DB_PASSWORD    | your MySQL password                                                                        |
| JWT_SECRET     | any long random string (32+ chars)                                                         |
| CORS_ORIGINS   | comma-separated list of allowed frontend URLs, e.g. `https://peerhub.vercel.app`          |

4. Build command: `./mvnw clean package -DskipTests`
5. Start command: `java -jar target/peerhub-backend-1.0.0.jar`

> **Tip for Render free tier MySQL:** Use [PlanetScale](https://planetscale.com) or [Railway](https://railway.app) for a free hosted MySQL instance, then paste the JDBC URL above.

---

## Connecting the Frontend

The React frontend (`src/api/client.js`) uses `/api` as the base path.
- **Locally:** Vite's proxy (`vite.config.js`) forwards `/api` → `http://localhost:8080/api`.
- **In production:** Set `VITE_API_BASE` env var in your frontend deployment to the full Render backend URL, e.g. `https://peerhub-backend.onrender.com/api`.

Update `src/api/client.js`:
```js
const API_BASE = import.meta.env.VITE_API_BASE || '/api';
```
