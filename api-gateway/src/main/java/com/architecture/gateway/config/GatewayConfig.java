package com.architecture.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    /**
     * Definición de Rutas y Filtros
     * Fuente:,
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Ruta para el Order Service (Servicio de Pedidos)
                .route("order-service", p -> p
                        .path("/orders/**") // 1. Si la petición empieza con /orders...
                        .filters(f -> f
                                // 2. Aplicamos el Rate Limiter (El Portero) antes de pasar la petición
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()))
                        )
                        .uri("lb://order-service")) // 3. Enrutamos al servicio usando Load Balancing (lb)

                .route("item-service", p -> p
                        .path("/api/items/**")
                        .filters(f -> f
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()))
                        )
                        .uri("lb://item-service"))

                .route("notification-service", p -> p
                        .path("/notifications/**")
                        .filters(f -> f
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()))
                        )
                        .uri("lb://notification-service"))

                // Aquí agregaríamos más rutas para item-service, product-service, etc.
                .build();
    }

    /**
     * Configuración del "Portero" (Redis Rate Limiter)
     * Fuente:,
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        // replenishRate (10): Cuántas peticiones permitimos por segundo (tasa normal).
        // burstCapacity (20): Cuántas permitimos de golpe en un pico de tráfico (ráfaga).
        return new RedisRateLimiter(10, 20);
    }

    /**
     * KeyResolver: Identificación del Usuario
     * Nota del Arquitecto: Aunque las fuentes se centran en el RateLimiter,
     * Spring Cloud Gateway necesita saber "A QUIÉN" limitar (IP, Usuario, etc.).
     * Por defecto, aquí usaremos una estrategia simple basada en el usuario (o "anonymous" si no hay).
     */
    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just("user-1"); // Simplificado para el MVP
    }
}
