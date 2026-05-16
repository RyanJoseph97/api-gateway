package com.eventmaster.config;

import org.springframework.context.annotation.Configuration;

/**
 * Placeholder for per-route rate limiting.
 *
 * Intended implementation: Redis-backed RequestRateLimiter using
 * spring-cloud-starter-gateway's built-in RedisRateLimiter. Requires adding
 * spring-boot-starter-data-redis-reactive and a Redis service to docker-compose.
 *
 * Example config when ready:
 *   .route("event-service", r -> r.path("/events/**")
 *       .filters(f -> f.requestRateLimiter(c -> {
 *           c.setRateLimiter(redisRateLimiter());
 *           c.setKeyResolver(userKeyResolver());
 *       }))
 *       .uri(eventServiceUrl))
 */
@Configuration
public class RateLimitingConfig {
    // TODO: implement RedisRateLimiter once Redis is added to the stack
}
