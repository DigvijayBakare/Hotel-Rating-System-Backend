# Hotel Rating System — Backend Overview

Spring Boot microservices project (Spring Boot **3.4.3**, Spring Cloud), consisting of six independent services under the parent folder `HotelRatingSystem/`.

## Architecture at a Glance

```
                        ┌───────────────────┐
                        │  ServiceRegistry   │  (Eureka Server)
                        └─────────▲──────────┘
                                  │ registers
        ┌───────────────┬────────┴────────┬────────────────┐
        │               │                 │                │
  ┌─────▼─────┐   ┌─────▼─────┐    ┌──────▼─────┐   ┌───────▼──────┐
  │ ApiGateway │   │UserService│    │HotelService│   │RatingService │
  └─────▲──────┘   └─────┬─────┘    └────────────┘   └──────────────┘
        │                │ Feign calls (with token)         ▲
   client requests        └──────────────────────────────────┘
        │
   ┌────▼─────┐
   │ConfigServer│  (Spring Cloud Config — currently minimal)
   └───────────┘
```

Client → **ApiGateway** (port `8084`) → routes to `UserService` / `HotelService` / `RatingService` via Eureka service discovery (`lb://` load-balanced URIs). Services register themselves with **ServiceRegistry** (Eureka) and optionally pull shared config from **ConfigServer**.

---

## Services

### 1. ServiceRegistry
- **Purpose:** Eureka discovery server — the "phone book" all other services register with and query.
- **Key dependency:** `spring-cloud-starter-netflix-eureka-server`
- **Config:** `application.properties` — only sets `spring.application.name=ServiceRegistry`.

### 2. ConfigServer
- **Purpose:** Intended central Spring Cloud Config server for externalized configuration.
- **Key dependency:** `spring-cloud-config-server`
- **Status:** Scaffolded but not yet actively serving property sources to other services — most services still keep their own local `application.properties`.

### 3. ApiGateway
- **Purpose:** Single entry point for all client traffic; reactive gateway (WebFlux) that routes requests to backend services and performs authentication.
- **Key dependencies:** `spring-cloud-starter-gateway`, `spring-boot-starter-webflux`, `spring-cloud-starter-netflix-eureka-client`, `spring-boot-starter-security`, `okta-spring-boot-starter`
- **Port:** `8084`
- **Routes (`application.yaml`):**
  | Path pattern | Target service |
  |---|---|
  | `/users/**` | `USERSERVICE` |
  | `/hotel/**`, `/staffs/**` | `HOTELSERVICE` |
  | `/ratings/**` | `RATINGSERVICE` |
- **Security (`SecurityConfig.java`):** All exchanges require authentication; configured as both an OAuth2 client (to log users in via Okta) and an OAuth2 resource server (validates incoming JWTs).
- **`AuthController`:** Exposes `GET /auth/login` — after Okta OIDC login, returns a JSON payload (`AuthResponse`) with the user's email, access token, refresh token, expiry, and granted authorities.
- ⚠️ **Note:** `application.yaml` currently has a live Okta `client-id`/`client-secret` committed in plaintext — should be moved to environment variables / a secrets manager and rotated.

### 4. UserService
- **Purpose:** Manages user profiles; also aggregates hotel/rating data via Feign clients.
- **Key dependencies:** `spring-boot-starter-data-jpa`, `postgresql`, `spring-cloud-starter-openfeign`, `spring-boot-starter-actuator`, `spring-boot-starter-aop`, `resilience4j-spring-boot3`, `spring-boot-starter-oauth2-client`, `spring-boot-starter-security`, `okta-spring-boot-starter`
- **Data store:** PostgreSQL (via JPA), table `User_info`
- **Entity — `Users`:** `userId`, `userName`, `email`, `about`, `usersAge`, plus a transient (non-persisted) `ratings` list.
- **Security (`WebSecurityConfig.java`):** All requests require authentication; validates JWTs as an OAuth2 resource server.
- **Service-to-service auth (`FeignClientInterceptor.java`):** Attaches a Bearer token (via `OAuth2AuthorizedClientManager`, client registration `my-internal-client`) to outgoing Feign requests to Hotel/Rating services.
- **External API clients:** `HotelService` and `RatingService` Feign interfaces (in `externalapi/`) call out to the other microservices.
- **REST endpoints (`/users`):**
  | Method | Path | Description |
  |---|---|---|
  | POST | `/users` | Create a user |
  | GET | `/users/{userId}` | Get a user (rate-limited via Resilience4j `@RateLimiter`, with a dummy-user fallback if throttled) |
  | GET | `/users` | List all users |
  | PUT | `/users/{userId}` | Update a user |
  | DELETE | `/users/{userId}` | Delete a user |
- **Resilience:** `@RateLimiter` is active on `getUser`; `@CircuitBreaker`/`@Retry` annotations exist in comments (not yet enabled).

### 5. HotelService
- **Purpose:** Manages hotel records and a stubbed staff listing.
- **Key dependencies:** `spring-boot-starter-data-jpa`, `postgresql`, `spring-cloud-starter-netflix-eureka-client`, `spring-cloud-starter-config`
- **Data store:** PostgreSQL (via JPA), table `hotels`
- **Entity — `Hotel`:** `hotelId`, `hotelName`, `location`, `about`
- **REST endpoints (`/hotel`):**
  | Method | Path | Description |
  |---|---|---|
  | POST | `/hotel` | Create a hotel |
  | GET | `/hotel` | List all hotels |
  | GET | `/hotel/{hotelId}` | Get a hotel by ID |
  | PUT | `/hotel/{hotelId}` | Update a hotel |
  | DELETE | `/hotel/{hotelId}` | Delete a hotel |
- **`StaffController` (`/staffs`):** `GET /staffs` — currently returns a hardcoded list of staff names (`Ram, Shyam, Sita, Gita, Rahul`); not backed by a database yet.
- **Error handling:** `HotelNotFoundException` + `GlobalExceptionHandler`.

### 6. RatingService
- **Purpose:** Manages user-submitted hotel ratings and feedback.
- **Key dependencies:** `spring-boot-starter-data-mongodb`, `spring-cloud-starter-netflix-eureka-client`, `spring-cloud-starter-config`
- **Data store:** MongoDB, collection `rating`
- **Entity — `Rating`:** `ratingId`, `userId`, `hotelId`, `rating` (int), `feedback`
- **REST endpoints (`/ratings`):**
  | Method | Path | Description |
  |---|---|---|
  | POST | `/ratings` | Create a rating |
  | GET | `/ratings` | List all ratings |
  | GET | `/ratings/user/{userId}` | List ratings by user |
  | GET | `/ratings/hotel/{hotelId}` | List ratings by hotel |
  | PUT | `/ratings/rating/{ratingId}` | Update a rating |
  | DELETE | `/ratings/{ratingId}` | Delete a rating |
- **Error handling:** `RatingNotFoundException`.

---

## Cross-Cutting Concerns

- **Service discovery:** Netflix Eureka (client in every business service, server in `ServiceRegistry`).
- **Authentication/Authorization:** Okta (OAuth2/OIDC) — login happens at the gateway; JWTs are validated downstream as resource servers.
- **Inter-service communication:** OpenFeign (used by `UserService` to call `HotelService`/`RatingService`), with a bearer-token-attaching interceptor for service-to-service auth.
- **Resilience:** Resilience4j — rate limiting is active; circuit breaker/retry are present but mostly commented out/unused so far.
- **Persistence:** Two data stores — PostgreSQL (`UserService`, `HotelService`) and MongoDB (`RatingService`).
- **Build tool:** Maven, all modules on Spring Boot `3.4.3` / Java (Jakarta EE namespace, so Java 17+).

## Known Gaps / TODOs (as observed in code)
- Config Server isn't yet centralizing configuration — each service still has its own `application.properties`.
- `StaffController` in HotelService returns hardcoded data, not persisted staff records.
- Circuit breaker and retry logic exist only as commented-out annotations in `UserService`.
- Okta client secret is hardcoded in `ApiGateway/src/main/resources/application.yaml` — should be externalized and rotated.
- `OAuth2AuthorizedClientManager` bean in `UserService/ConfigurationClass.java` is commented out but is required by `FeignClientInterceptor` — this may currently fail at runtime unless defined elsewhere.
