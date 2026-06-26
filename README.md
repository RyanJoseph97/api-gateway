# api-gateway

Single entry point for the EventMaster UI. Validates JWTs at the boundary, rewrites paths to each downstream service, and enforces rate limiting.

Port: `8090`.

## Routing Table

| Gateway path | Routed to | Rewritten to |
|---|---|---|
| `GET /users/*/saved-events` | event-service:8081 | `/event-service/users/*/saved-events` |
| `GET /users/*/rsvped-events` | event-service:8081 | `/event-service/users/*/rsvped-events` |
| `/users`, `/users/**` | user-service:8080 | `/user-service/users/**` |
| `/events`, `/events/**` | event-service:8081 | `/event-service/events/**` |
| `/recommendations`, `/recommendations/**` | recommendation-service:8082 | `/recommendation-service/recommendations/**` |
| `/feed`, `/feed/**` | feed-service:8083 | `/feed-service/feed/**` |

The saved-events and rsvped-events routes are declared first because they match a narrower path pattern than the generic `/users/**` rule.

## Public Paths (no JWT required)

- `POST /users` — registration
- `POST /users/login` — login
- `POST /users/token/refresh` — token refresh
- `POST /users/logout` — logout
- `GET /events/**` — event browse/detail
- `GET /users/search` — user search
- `/swagger-ui/**`, `/v3/api-docs/**`, `/webjars/swagger-ui/**` — Swagger UI

All other paths require a valid `Authorization: Bearer <token>` header.

## JWT Validation

`JwtAuthenticationGlobalFilter` runs at order `-1` (before all route filters). It validates the Bearer token using the shared `JWT_SECRET`. On success it forwards an `X-Authenticated-User` header containing the token subject (username) to downstream services. Invalid or missing tokens receive `401 Unauthorized`.

## Rate Limiting

Redis-backed `RedisRateLimiter` is applied to all routes:
- Replenish rate: **10 tokens/sec** per IP
- Burst capacity: **20 tokens**
- Cost: 1 token per request

Requires Redis on `localhost:6379` (local) or configured via `REDIS_HOST`/`REDIS_PASSWORD` (Docker).

## CORS

Configured globally in `application.yml`. By default `allowedOriginPatterns: "*"` is used. In production set `CORS_ALLOWED_ORIGINS` to restrict to the UI origin.

## Swagger UI

Aggregated Swagger UI is available at `http://localhost:8090/swagger-ui.html`. It proxies OpenAPI specs from each downstream service.

## Running Locally

Requires Redis:

```bash
# Start Redis (Docker)
docker run -p 6379:6379 redis:7-alpine

cd api-gateway
mvn spring-boot:run
```

Available at `http://localhost:8090`.

## Environment Variables

| Variable | Required | Default | Notes |
|----------|----------|---------|-------|
| `JWT_SECRET` | No | `eventmaster-shared-dev-secret-key-change-in-prod` | Must match all services |
| `CORS_ALLOWED_ORIGINS` | No | `*` | Restrict in production |
| `REDIS_PASSWORD` | Docker only | — | Redis auth password |

## Testing

```bash
mvn test
```
