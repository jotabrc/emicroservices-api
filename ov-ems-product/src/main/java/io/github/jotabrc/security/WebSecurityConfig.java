package io.github.jotabrc.security;

import io.github.jotabrc.ov_auth_validator.util.UserRoles;
import io.github.jotabrc.ovauth.jwt.TokenGlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
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
            "/v3/api-docs-product/**",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-product.html",
            "/swagger-product/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**"
    };

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/product/get/name/").permitAll()
                        .requestMatchers(MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/product/get/category/").permitAll()
                        .requestMatchers(MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/product/get/uuid/").permitAll()
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()

                        .requestMatchers(MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/product/add")
                        .hasAnyRole(UserRoles.ADMIN.getName(), UserRoles.SYSTEM.getName())

                        .requestMatchers(MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/product/update")
                        .hasAnyRole(UserRoles.ADMIN.getName(), UserRoles.SYSTEM.getName())

                        .requestMatchers(MAPPING_PREFIX + MAPPING_VERSION_SUFFIX + "/product/update/price")
                        .hasAnyRole(UserRoles.ADMIN.getName(), UserRoles.SYSTEM.getName())
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
