package io.github.jotabrc.security;

import io.github.jotabrc.ov_auth_validator.util.UserRoles;
import io.github.jotabrc.ovauth.jwt.TokenGlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static io.github.jotabrc.controller.ControllerDefaults.MAPPING_PREFIX;
import static io.github.jotabrc.controller.ControllerDefaults.MAPPING_VERSION_SUFFIX;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs-order/**",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-order.html",
            "/swagger-order/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**"
    };

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(
                                MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/order/add",
                                MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/order/get/uuid/",
                                MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/order/get/user/uuid/",
                                MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/order/update",
                                MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/order/cancel/uuid/",
                                MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/order/return"
                        ).hasAnyRole(UserRoles.SYSTEM.getName(), UserRoles.ADMIN.getName(), UserRoles.USER.getName())
                        .anyRequest().authenticated()
                )
                .addFilterAfter(new TokenGlobalFilter(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Authorization", "Content-Type")
                        .exposedHeaders("Authorization")
                        .allowCredentials(true);
            }
        };
    }
}
