# Cadence — Project Management REST API
> 🔗 **Live API:** [Swagger UI](https://cadence-t6ui.onrender.com/swagger-ui.html) — the free tier sleeps when idle, so the **first request may take ~40s** to wake. Register, click **Authorize**, then try any endpoint.

> A backend-focused, production-style project management API (think a lightweight Jira/Trello engine) built with **Java 21** and **Spring Boot 3.5**. Teams create **projects**, break work into **tasks/user stories**, assign developers, and move work through an **enforced status workflow** — all behind **JWT-secured**, well-documented REST endpoints.

<p align="left">
  <img alt="Java" src="https://img.shields.io/badge/Java-21-orange">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen">
  <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-16-blue">
  <img alt="Build" src="https://img.shields.io/badge/CI-GitHub%20Actions-black">
</p>

---

## Table of Contents
- [Overview](#overview)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Data Model (ER Diagram)](#data-model-er-diagram)
- [Status Workflow Engine](#status-workflow-engine)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Core Endpoints](#core-endpoints)
- [Testing](#testing)
- [CI/CD](#cicd)
- [Project Structure](#project-structure)
- [Concepts Demonstrated](#concepts-demonstrated)
- [Roadmap](#roadmap)

---

## Overview

Cadence is a REST API for managing software projects and their work items. It is intentionally **backend-only** — the focus is clean architecture, domain modelling, security, and API design rather than UI. The API is fully explorable via **Swagger UI**.

A **manager** oversees projects, a **team of developers** (many-to-many) collaborates on them, and each project contains **tasks** (user stories) with story points, due dates, assignees, and a strict **status lifecycle** enforced on the server.

---

## Key Features

- **JWT authentication** — register/login issue signed tokens; all business endpoints are protected.
- **Role-ready users** with BCrypt-hashed passwords.
- **Projects** with a manager (owner), a many-to-many developer team, description, and target due date.
- **Tasks / user stories** with story points, due dates, optional assignee, and status.
- **Enforced status workflow** — a shared state machine rejects illegal transitions (e.g. you cannot skip straight from `TODO` to `DONE`).
- **Cross-entity business rules** — e.g. a task's due date cannot exceed its project's due date.
- **Derived fields** — `remainingDays`, `overdue`, and a project's `totalStoryPoints` are computed, never stored stale.
- **Consistent error handling** — one global handler returns clean JSON with correct HTTP status codes (400 / 404 / 409 / 422 / 500).
- **Caching** on hot reads with eviction on writes.
- **Interactive API docs** via Swagger / OpenAPI.
- **Containerized** — the whole stack (app + PostgreSQL) runs with a single `docker compose up`.
- **CI pipeline** — every push builds and runs the test suite on GitHub Actions.

---

## Tech Stack

| Area | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 (Web, Data JPA, Security, Validation, Cache) |
| Persistence | PostgreSQL 16, Hibernate ORM |
| Auth | Spring Security + JWT (jjwt), BCrypt |
| Docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5, Mockito |
| Build | Maven |
| DevOps | Docker, Docker Compose, GitHub Actions |

---

## Architecture

Cadence follows a clean **layered architecture** with dependencies pointing inward:

```
Client ──► Controller (REST / HTTP)  ──►  Service (business logic)  ──►  Repository (Spring Data JPA)  ──►  PostgreSQL
                     │                              │
                  DTOs + @Valid            interfaces + @Transactional
```

- **Controllers** handle HTTP only and speak **DTOs** (never expose JPA entities).
- **Services** hold business rules and coordinate transactions; they depend on **interfaces** (SOLID / dependency inversion).
- **Repositories** are Spring Data JPA interfaces.
- Cross-cutting concerns — **security**, **exception handling**, and the **status workflow engine** — live in dedicated packages and are reused everywhere (DRY).

---

## Data Model (ER Diagram)

![Cadence ER Diagram](https://github.com/atandritC/Project-Demos/blob/main/Cadence_ER-Diagram.png)

- `users` — accounts (email, hashed password, role).
- `projects` — owned by a manager (`@ManyToOne`), with a developer team (`project_members`, `@ManyToMany`).
- `tasks` — belong to a project (`@ManyToOne`, bidirectional with cascade + orphan removal), optionally assigned to a user.

---

## Status Workflow Engine

Both **tasks** and **projects** move through a **finite state machine** enforced by a single generic engine (`WorkflowStatus` + `StatusWorkflow`). Illegal transitions are rejected with `409 Conflict`.

![Cadence Status Workflows](https://github.com/atandritC/Project-Demos/blob/main/cadence-status-workflows.svg)

**Rules (shared by both workflows):**
- A **forward-only** happy path — no skipping steps.
- A **pausable** side-state (`BLOCKED` / `ON_HOLD`) that can only **resume to the exact state it came from**.
- **CANCELLED** is reachable from **any non-final state**.
- Final states (`DONE`/`COMPLETED` and `CANCELLED`) have **no exit**.

---

## Getting Started

### Prerequisites
- Java 21
- Maven (or use the bundled `./mvnw`)
- PostgreSQL 16 **or** Docker

### Option A — Run with Docker (recommended)

```bash
docker compose up --build
```

This starts PostgreSQL and the app together. The API is available at `http://localhost:8080`.

### Option B — Run locally

1. Create the database:
   ```sql
   CREATE DATABASE cadence;
   CREATE USER cadence WITH PASSWORD 'cadence';
   GRANT ALL ON SCHEMA public TO cadence;
   ```
2. Start the app:
   ```bash
   ./mvnw spring-boot:run
   ```

Configuration lives in `src/main/resources/application.properties` (DB connection and JWT settings).

---

## API Documentation

Interactive docs are served by Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

**To try protected endpoints:**
1. Call `POST /cadence/auth/register` (or `/login`) and copy the returned `token`.
2. Click **Authorize** in Swagger UI and paste the token.
3. All requests now include the `Authorization: Bearer <token>` header.

> A read-only rendering of the API docs is also available on GitHub Pages (via the committed OpenAPI spec).

---

## Core Endpoints

> All endpoints except `auth` and the Swagger/OpenAPI routes require a JWT.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/cadence/auth/register` | Create an account, returns a JWT |
| `POST` | `/cadence/auth/login` | Log in, returns a JWT |
| `GET`  | `/cadence/users` | List users |
| `GET`  | `/cadence/users/{id}` | Get a user |
| `POST` | `/cadence/projects` | Create a project |
| `GET`  | `/cadence/projects/{id}` | Get a project |
| `GET`  | `/cadence/projects?managerId={id}` | List projects by manager |
| `POST` | `/cadence/projects/{id}/members/{userId}` | Add a developer to the team |
| `PATCH`| `/cadence/projects/{id}/status` | Change project status (workflow-checked) |
| `POST` | `/cadence/tasks` | Create a task / user story |
| `GET`  | `/cadence/tasks/{id}` | Get a task |
| `GET`  | `/cadence/tasks?projectId={id}` | List a project's tasks |
| `PATCH`| `/cadence/tasks/{id}/status` | Change task status (workflow-checked) |

---

## Testing

```bash
./mvnw test
```

- **Unit tests** for the service layer using **Mockito** (dependencies mocked, logic tested in isolation).
- **Pure unit tests** for the status-workflow state machine (all transition rules).

---

## CI/CD

Every push and pull request to `main` triggers a **GitHub Actions** pipeline that:
1. Checks out the code and sets up JDK 21.
2. Spins up a PostgreSQL service container.
3. Runs `./mvnw clean verify` (compile + tests).

A failing test fails the build, keeping `main` always healthy.

---

## Project Structure

```
src/main/java/com/cadence
├── common/           # BaseEntity, exceptions, workflow engine, shared helpers
├── security/         # JWT, Spring Security config, auth endpoints
├── user/             # User entity, repo, service, controller, DTOs
├── project/          # Project domain (manager, members, status)
└── task/             # Task domain (status, story points, assignee)
```

---

## Concepts Demonstrated

- **Core Java:** OOP (encapsulation, inheritance via `BaseEntity`, polymorphism, abstraction), exception handling, collections, enums.
- **Java 8+:** Streams, lambdas, method references, `Optional`, records for DTOs.
- **Spring Boot:** dependency injection, bean lifecycle, Spring Data JPA, Spring Security, caching, validation, global exception handling.
- **JPA/Hibernate:** entity relationships (`@ManyToOne`, `@OneToMany`, `@ManyToMany`), owning vs inverse side, cascade & orphan removal, lazy vs eager, fixing the N+1 problem with `JOIN FETCH`.
- **API design:** RESTful resources, DTO boundaries, correct status codes, OpenAPI docs.
- **Engineering practices:** layered architecture, SOLID, a reusable (DRY) workflow engine, unit testing, Dockerization, and CI.

---

## Roadmap

- Role-based authorization (`@PreAuthorize`) for admin-only operations
- Pagination, filtering, and sorting on list endpoints
- Comments and activity feed on tasks
- Integration tests with Testcontainers
- Deployment to a cloud PaaS with a live demo URL

---

*Built as a hands-on study of production-grade Spring Boot backend development.*
