package com.demo.sell_card_demo1.config;

import com.demo.sell_card_demo1.entity.User;
import com.demo.sell_card_demo1.exception.AuthenticationException;
import com.demo.sell_card_demo1.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    TokenService tokenService;

    // Danh sách các API không cần check Token
    private final List<String> PUBLIC_API = List.of(
            "POST:/api/register",
            "POST:/api/login",
            "POST:/api/forgot-password",
            "GET:/api/products",
            "GET:/api/products/**",
            "GET:/swagger-ui/**",
            "GET:/v3/api-docs/**",
            "GET:/api/payment/**",             // Cho phép Success và Cancel
            "POST:/api/payment/**",            // Dự phòng
            "POST:/api/v1/payment-webhook/**",
            "GET:/v3/api-docs.yaml",
            "GET:/swagger-resources/**",
            "GET:/webjars/**",
            "GET:/api/products/branch/**",
            "GET:/api/branches",
            "GET:/ws-chat/**",
            "GET:/ws-chat",
            "GET:/ws/**",
            "GET:/ws",

            "GET:/payment-success.html",
            "GET:/payment-cancel.html"
    );

    public boolean isPublicAPI(String uri, String method) {
        AntPathMatcher matcher = new AntPathMatcher();
        return PUBLIC_API.stream().anyMatch(pattern -> {
            String[] parts = pattern.split(":", 2);
            if (parts.length != 2) return false;
            String allowedMethod = parts[0];
            String allowedUri = parts[1];
            return method.equalsIgnoreCase(allowedMethod) && matcher.match(allowedUri, uri);
        });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        if (uri.contains("/api/payment") || uri.contains("/api/payment-webhook")) {
            filterChain.doFilter(request, response);
            return; // Quan trọng: Return luôn để không chạy xuống đoạn check token bên dưới
        }

        if (isPublicAPI(uri, method)) {
            filterChain.doFilter(request, response);
            return; // Return luôn để tránh chạy xuống logic check token bên dưới
        }

        String token = getToken(request);
        if (token == null) {
            resolver.resolveException(request, response, null, new AuthenticationException("Empty token!") {});
            return;
        }

        User account;
        try {
            account = tokenService.extractAccount(token);
        } catch (ExpiredJwtException expiredJwtException) {
            resolver.resolveException(request, response, null, new AuthenticationException("Expired Token!"));
            return;
        } catch (MalformedJwtException malformedJwtException) {
            resolver.resolveException(request, response, null, new AuthenticationException("Invalid Token!"));
            return;
        }

        UsernamePasswordAuthenticationToken authenToken =
                new UsernamePasswordAuthenticationToken(account, token, account.getAuthorities());
        authenToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenToken);

        filterChain.doFilter(request, response);
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.substring(7);
    }
}