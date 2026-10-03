# Bovis

Monorepo containing the **Bovis** application: backend API and web frontend.

## Structure

```
bovis/
├── backend/    # Spring Boot application (Java 11, Maven, PostgreSQL)
└── frontend/   # Vue 3 application (Vuetify, Axios)
```

---

## Getting Started

### Backend (`backend/`)

Requirements: Java 11+, Maven (or use `./mvnw`)

```bash
cd backend
./mvnw clean spring-boot:run
```

API documentation (Swagger) is available at:
`http://localhost:8080/swagger-ui.html`

### Frontend (`frontend/`)

Requirements: Node.js (v16+) and npm

```bash
cd frontend
npm install
npm run serve
```

The frontend application will run on `http://localhost:3000`.
