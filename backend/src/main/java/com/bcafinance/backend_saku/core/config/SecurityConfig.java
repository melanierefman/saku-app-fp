package com.bcafinance.backend_saku.core.config;

import com.bcafinance.backend_saku.core.exception.SecurityExceptionHandler;
import com.bcafinance.backend_saku.core.filter.JwtAuthFilter;
import com.bcafinance.backend_saku.core.security.AppUserDetailService;
import com.bcafinance.backend_saku.core.security.CustomerUserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final SecurityExceptionHandler securityExceptionHandler;

        @Value("${app.security.cors-allowed-origins}")
        private List<String> allowedOrigins;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // RBAC & Endpoint Authorization
                                .authorizeHttpRequests(request -> request
                                                // Preflight CORS
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // Public Endpoints, Uploads & Scalar OpenAPI Documentation
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/api/public/**",
                                                                "/uploads/**",
                                                                "/scalar", "/scalar/**",
                                                                "/docs", "/docs/**",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**", "/swagger-ui.html"
                                                ).permitAll()

                                                // Role-Based Access Control
                                                .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                                                .requestMatchers("/api/marketing/**").hasRole("MARKETING")
                                                .requestMatchers("/api/branch-manager/**", "/api/branchmanager/**", "/api/bm/**").hasRole("BRANCHMANAGER")
                                                .requestMatchers("/api/backoffice/**", "/api/bo/**", "/api/scoring/**").hasRole("BACKOFFICE")
                                                .requestMatchers(
                                                                "/api/superadmin/**",
                                                                "/api/master/**",
                                                                "/api/karyawan/**",
                                                                "/api/cabang/**",
                                                                "/api/plafond/**",
                                                                "/api/role/**",
                                                                "/api/menu/**",
                                                                "/api/permission/**",
                                                                "/api/monitoring/**",
                                                                "/api/monitoring-pengajuan/**",
                                                                "/api/audit-log/**"
                                                ).hasRole("SUPERADMIN")

                                                // All other endpoints require authentication (e.g., /api/realtime/**)
                                                .anyRequest().authenticated())

                                .headers(headers -> headers
                                                // contentsecuritypolicy
                                                .referrerPolicy(referrer -> referrer
                                                                .policy(
                                                                                ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                                                .httpStrictTransportSecurity(hsts -> hsts
                                                                .includeSubDomains(true)
                                                                .maxAgeInSeconds(31536000))
                                                .frameOptions(frame -> frame.deny()))

                                .exceptionHandling(handling -> handling

                                                // 401
                                                .authenticationEntryPoint(
                                                                (request, response,
                                                                                authException) -> securityExceptionHandler
                                                                                                .handleAuthenticationException(
                                                                                                                response,
                                                                                                                authException))

                                                // 403
                                                .accessDeniedHandler(
                                                                (request, response,
                                                                                accessDeniedException) -> securityExceptionHandler
                                                                                                .handleAccessDeniedException(
                                                                                                                response,
                                                                                                                accessDeniedException)))

                                .formLogin(form -> form.disable())

                                .httpBasic(basic -> basic.disable())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                                .build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration konfigurasi = new CorsConfiguration();
                
                java.util.List<String> origins = new java.util.ArrayList<>();
                origins.add("https://saku-app-fp.vercel.app");
                origins.add("https://*.vercel.app");
                origins.add("http://localhost:*");
                origins.add("http://localhost:4200");
                origins.add("http://localhost:3000");
                origins.add("http://localhost:5173");
                origins.add("https://saku.morpkhai.web.id");
                origins.add("http://saku.morpkhai.web.id");
                origins.add("https://*.morpkhai.web.id");
                if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
                    for (String o : allowedOrigins) {
                        if (!origins.contains(o)) {
                            origins.add(o);
                        }
                    }
                }

                konfigurasi.setAllowedOriginPatterns(origins);
                konfigurasi.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
                konfigurasi.setAllowedHeaders(List.of("*"));
                konfigurasi.setExposedHeaders(List.of("Authorization", "Content-Type", "Set-Cookie"));
                konfigurasi.setAllowCredentials(true);
                konfigurasi.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource sumber = new UrlBasedCorsConfigurationSource();
                sumber.registerCorsConfiguration("/**", konfigurasi);
                return sumber;
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider karyawanAuthenticationProvider(
                        AppUserDetailService appUserDetailService,
                        PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(appUserDetailService);

                provider.setPasswordEncoder(passwordEncoder);

                return provider;
        }

        @Bean(name = "customerAuthenticationProvider")
        public DaoAuthenticationProvider customerAuthenticationProvider(
                        CustomerUserDetailService customerUserDetailService,
                        PasswordEncoder passwordEncoder) {

                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customerUserDetailService);

                provider.setPasswordEncoder(passwordEncoder);

                return provider;
        }

        @Bean(name = "karyawanAuthenticationManager")
        @Primary
        public AuthenticationManager karyawanAuthenticationManager(
                        @Qualifier("karyawanAuthenticationProvider") DaoAuthenticationProvider provider) {
                return new ProviderManager(provider);
        }

        @Bean(name = "customerAuthenticationManager")
        public AuthenticationManager customerAuthenticationManager(
                        @Qualifier("customerAuthenticationProvider") DaoAuthenticationProvider provider) {

                return new ProviderManager(provider);
        }

        @Bean
        ObjectMapper objectMapper() {
                return new ObjectMapper();
        }
}
