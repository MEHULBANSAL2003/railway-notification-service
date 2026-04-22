package com.railway.railway_notification_service.config;

import com.railway.common.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
      // Disable Spring Security's CORS processing — handled by
      // our standalone CorsFilter registered at highest precedence.
      .cors(AbstractHttpConfigurer::disable)

      // Disable CSRF — we're a stateless REST API using JWT,
      // not a server-rendered form-based app.
      .csrf(AbstractHttpConfigurer::disable)

      // Stateless sessions — no server-side sessions.
      // Every request is authenticated independently via JWT.
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .exceptionHandling(ex -> ex
        .authenticationEntryPoint((request, response, authException) -> {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
          response.setContentType("application/json");
          response.getWriter().write("{\"error\": \"Authentication required\"}");
        })
      )

      // Endpoint access rules
      .authorizeHttpRequests(auth -> auth

        .requestMatchers("/actuator/**")
        .permitAll()
        .anyRequest().authenticated()
      )

      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }


  @Bean
  public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000","http://localhost:5173","https://railtick.in","https://admin.railtick.in", "https://dev.railtick.in", "https://dev.admin.railtick.in" ));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return bean;
  }


}
