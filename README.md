# WinWin.travel Mini Test

Mini test task consisting of two Spring Boot REST API services and PostgreSQL.

## Architecture

The project consists of two independent Spring Boot services:

* **auth-api** — user registration, authentication with JWT and processing requests.
* **data-api** — text transformation service available only to `auth-api`.

PostgreSQL is used by `auth-api` to store users and processing logs.

### Request flow

```text
Client
  |
  | register / login
  v
auth-api
  |
  | POST /api/process
  | X-Internal-Token
  v
data-api
  |
  | transformed text
  v
auth-api
  |
  | save processing log
  v
PostgreSQL
```

## Technologies

* Java 17
* Spring Boot
* Spring Web
* Spring Security
* Spring Data JPA
* PostgreSQL
* JWT
* BCrypt
* Docker
* Docker Compose
* Swagger / OpenAPI
* Maven
* JUnit 5
* Mockito

## Running with Docker Compose

Build and start all services:

```bash
docker compose up --build

The following services will be started:

auth-api — http://localhost:8080
data-api — http://localhost:8081
PostgreSQL — localhost:5432

To stop the services:

docker compose down

## API Endpoints

### auth-api

#### Register

```http
POST /api/auth/register
```

Request:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

Returns `201 Created`.

#### Login

```http
POST /api/auth/login
```

Request:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

Returns `200 OK` with JWT token.

#### Process text

```http
POST /api/process
```

Requires JWT authentication:

```http
Authorization: Bearer <JWT>
```

Request:

```json
{
  "text": "hello"
}
```

The request is sent to `data-api` for transformation. The transformed result is returned and the processing information is saved to PostgreSQL.

### data-api

#### Transform text

```http
POST /api/transform
```

Requires the internal service token:

```http
X-Internal-Token: <INTERNAL_TOKEN>
```

Request:

```json
{
  "text": "hello"
}
```

Response:

```json
{
  "result": "HELLO"
}
```
## Swagger / OpenAPI

Swagger UI is available for both services.

### auth-api

http://localhost:8080/swagger-ui/index.html

### data-api

http://localhost:8081/swagger-ui/index.html

Swagger provides interactive API documentation and allows testing the available endpoints directly from the browser.

## Environment Variables

The application uses environment variables for configuration.

### auth-api

| Variable            | Description                                         |
| ------------------- | --------------------------------------------------- |
| `POSTGRES_URL`      | PostgreSQL JDBC connection URL                      |
| `POSTGRES_USER`     | PostgreSQL username                                 |
| `POSTGRES_PASSWORD` | PostgreSQL password                                 |
| `JWT_SECRET`        | Secret key used to sign JWT tokens                  |
| `INTERNAL_TOKEN`    | Shared token used for communication with `data-api` |
| `DATA_API_URL`      | Base URL of the `data-api` service                  |

### data-api

| Variable         | Description                                                |
| ---------------- | ---------------------------------------------------------- |
| `INTERNAL_TOKEN` | Shared token used to authenticate requests from `auth-api` |

For Docker Compose, these variables are configured in `docker-compose.yml`.

## Tests

The project contains unit and web-layer tests for the main application components.

Tests cover:

* user registration and duplicate registration;
* user login and invalid credentials;
* request validation;
* JWT authentication filter;
* communication between `auth-api` and `data-api`;
* text transformation;
* handling of unavailable `data-api`;
* processing requests and saving processing logs.

Run tests with Maven:

```bash
./mvnw test
```

For Windows:

```powershell
.\mvnw.cmd test
```

## Project Structure

```text
mini-test/
├── auth-api/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── data-api/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── docker-compose.yml
└── README.md
```

### Services

* `auth-api` — authentication, JWT authorization, request processing and processing logs.
* `data-api` — text transformation service.
* `postgres` — database used by `auth-api`.
