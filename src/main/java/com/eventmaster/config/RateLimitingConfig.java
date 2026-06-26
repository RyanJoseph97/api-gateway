package com.eventmaster.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitingConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        // replenishRate=10 tokens/sec, burstCapacity=20, requestedTokens=1 per request
        return new RedisRateLimiter(10, 20, 1);
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            var address = exchange.getRequest().getRemoteAddress();
            return Mono.just(address != null ? address.getAddress().getHostAddress() : "unknown");
        };
    }
}
