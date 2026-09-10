# Resource Booking API

Production-oriented Spring Boot 3.3 REST API for booking priced resources. The
application uses Java 17, JPA, stateless JWT authentication, BCrypt password
hashing, and PostgreSQL or MySQL (H2 is the default for local development and
tests).

## Requirements and quick start

* Java 17+
* Maven 3.9+
* PostgreSQL 14+ or MySQL 8+ for a persistent database

Run with the development H2 database:

```bash
mvn spring-boot:run
```

The default seed accounts are `admin` / `Admin123!` and `user` / `User123!`.
Change these values immediately outside local development.

Build and test:

```bash
mvn test
mvn package
```

## Configuration

Configuration is supplied through environment variables (defaults are suitable
only for development):

| Variable | Default | Description |
| --- | --- | --- |
| `DB_URL` | `jdbc:h2:mem:booking;DB_CLOSE_DELAY=-1;MODE=PostgreSQL` | JDBC URL |
| `DB_USERNAME` | `sa` | Database username |
| `DB_PASSWORD` | empty | Database password |
| `DB_DRIVER` | `org.h2.Driver` | `org.postgresql.Driver` or `com.mysql.cj.jdbc.Driver` |
| `DDL_AUTO` | `update` | Hibernate schema mode; use `validate` in production |
| `JWT_SECRET` | development placeholder | Random secret of at least 32 bytes |
| `JWT_EXPIRATION_MS` | `3600000` | Token lifetime |
| `SEED_ENABLED` | `true` | Create initial users and resource |
| `SEED_ADMIN_USERNAME/PASSWORD` | `admin` / `Admin123!` | Initial admin |
| `SEED_USER_USERNAME/PASSWORD` | `user` / `User123!` | Initial user |

Example PostgreSQL settings:

```bash
DB_URL=jdbc:postgresql://localhost:5432/booking \
DB_USERNAME=booking DB_PASSWORD=secret \
DB_DRIVER=org.postgresql.Driver DDL_AUTO=validate \
JWT_SECRET="$(openssl rand -base64 48)" mvn spring-boot:run
```

## Authentication

Call `POST /auth/login`:

```json
{"username":"user","password":"User123!"}
```

The response contains a signed HS256 JWT. Send it on subsequent calls:
`Authorization: Bearer <token>`. The server validates the signature, expiry, and
subject, then loads the current user and role from the database; request body
user IDs are never trusted.

## API

All successful list endpoints return Spring's `Page` shape (`content`,
`pageable`, `totalElements`, and related metadata). `page` is zero based,
`size` defaults to 20, and `sort` is optional (for example
`sort=price,desc`).

### Resources

| Method | Path | Access | Result |
| --- | --- | --- | --- |
| GET | `/resources` | USER, ADMIN | Paged resource list |
| GET | `/resources/{id}` | USER, ADMIN | Resource |
| POST | `/resources` | ADMIN | 201, create resource |
| PUT | `/resources/{id}` | ADMIN | Update resource |
| DELETE | `/resources/{id}` | ADMIN | 204, delete resource |

Resource input has `name`, optional `description`, non-negative `price` with
two decimal places, and optional `available` (defaults to true).

### Reservations

| Method | Path | Access | Result |
| --- | --- | --- | --- |
| GET | `/reservations` | USER, ADMIN | USER sees only own; ADMIN sees all |
| GET | `/reservations/{id}` | owner, ADMIN | Ownership-protected detail |
| POST | `/reservations` | USER, ADMIN | 201, creates as authenticated user |
| PUT | `/reservations/{id}` | ADMIN | Change status |
| DELETE | `/reservations/{id}` | ADMIN | 204 |

Reservation creation takes `resourceId`, `startDate`, and `endDate`.
`endDate` must be after `startDate` and the resource must be available.
The resource price is copied to the reservation as a decimal snapshot, so
historical reservations and price filters are unaffected by later resource
price changes. Statuses are `PENDING`, `CONFIRMED`, and `CANCELLED`.

Filtering is available on the list endpoint:

```text
/reservations?status=PENDING&minPrice=10.00&maxPrice=100.00&page=0&size=10&sort=createdAt,desc
```

## Errors and API documentation

Validation, authorization, not-found, and business errors return a consistent
JSON object with `timestamp`, `status`, `error`, `message`, `path`, and
`fieldErrors` (when applicable). HTTP codes include 400, 401, 403, 404, and
409/500 as appropriate.

OpenAPI UI is available at `/swagger-ui.html`; the specification is at
`/v3/api-docs`. Swagger endpoints are public, while business endpoints require
JWT authentication.

## Package layout

Code is organized into `controller`, `service`, `repository`, `dto`, `entity`,
`security`, `config`, and `exception` packages. Integration tests cover
authentication, role restrictions, ownership, and reservation price filtering.
