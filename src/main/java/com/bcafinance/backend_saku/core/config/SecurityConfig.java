package com.bcafinance.backend_saku.core.config;

import com.bcafinance.backend_saku.core.exception.SecurityExceptionHandler;
import com.bcafinance.backend_saku.core.exception.UnauthorizedHandler;
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
        SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter,
                        UnauthorizedHandler unauthorizedHandler) throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // untuk RBAC
                                .authorizeHttpRequests(request -> request
                                                .requestMatchers("/api/auth/**", "/api/public/**", "/uploads/**").permitAll()
                                                .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                                                .requestMatchers("/api/marketing/**").hasRole("MARKETING")
                                                .requestMatchers("/api/branch-manager/**", "/api/bm/**",
                                                                "/api/branchmanager/**")
                                                .hasRole("BRANCHMANAGER")
                                                .requestMatchers("/api/karyawan/**", "/api/cabang/**",
                                                                "/api/plafond/**", "/api/master/**",
                                                                "/api/role/**", "/api/menu/**", "/api/permission/**",
                                                                "/api/monitoring/**")
                                                .hasRole("SUPERADMIN")
                                                .requestMatchers("/api/backoffice/**").hasRole("BACKOFFICE")

                                                .anyRequest()
                                                .authenticated())

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
                konfigurasi.setAllowedOrigins(allowedOrigins);
                konfigurasi.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                konfigurasi.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                konfigurasi.setAllowCredentials(true);
                konfigurasi.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource sumber = new UrlBasedCorsConfigurationSource();
                sumber.registerCorsConfiguration("/api/**", konfigurasi);
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
