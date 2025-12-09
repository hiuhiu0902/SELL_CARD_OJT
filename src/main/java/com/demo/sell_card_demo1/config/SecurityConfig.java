package com.demo.sell_card_demo1.config;

import com.demo.sell_card_demo1.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    Filter filter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // --- CẤU HÌNH CORS CHUẨN ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. Chỉ định rõ domain Frontend (Localhost hoặc Vercel...)
        // KHÔNG dùng "*" nếu có allowCredentials(true)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // 2. Cho phép các method
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 3. Cho phép các Header quan trọng (đặc biệt là Authorization)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "x-auth-token"));

        // 4. Cho phép gửi Credentials (Cookie, Auth Header)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationService authenticationService) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // Dòng này kích hoạt bean corsConfigurationSource ở trên
                .authorizeHttpRequests(
                        req -> req
                                .requestMatchers(
                                        "/api/login",
                                        "/api/register",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/api/forgot-password",
                                        "/payment-success.html",
                                        "/payment-cancel.html",
                                        "/products/**",
                                        "/branches/**",
                                        "/ws/**"
                                ).permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/member/**").hasRole("MEMBER")
                                .anyRequest().authenticated()
                )
                .userDetailsService(authenticationService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}