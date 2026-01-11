package com.architecture.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity // Habilita seguridad reactiva para el Gateway
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Deshabilitamos CSRF para APIs REST stateless
                .authorizeExchange(exchange -> exchange
                        // Permitimos acceso libre a Eureka (opcional, para debugging)
                        .pathMatchers("/eureka/**").permitAll()
                        // CUALQUIER otra petición requiere un Token válido
                        .anyExchange().authenticated()
                )
                // Habilitamos que actúe como Resource Server validando JWTs
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

        return serverHttpSecurity.build();
    }
}
