package com.misakguambshop.app.config;

import com.misakguambshop.app.security.CustomUserDetailsService;
import com.misakguambshop.app.security.JwtAuthenticationEntryPoint;
import com.misakguambshop.app.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(mvcMatcherBuilder.pattern("/api/auth/**")).permitAll()
                                .requestMatchers(mvcMatcherBuilder.pattern("/api/auth/signup/user")).permitAll()
                                .requestMatchers(mvcMatcherBuilder.pattern("/api/auth/signup/seller")).permitAll()
                                .requestMatchers(mvcMatcherBuilder.pattern("/api/sellers/**")).hasAnyRole("ADMIN", "SELLER")
                                .requestMatchers(mvcMatcherBuilder.pattern("/api/users/**")).authenticated()
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/categories/**")).hasAnyAuthority("ADMIN", "SELLER", "USER")
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/categories/**")).hasAuthority("ADMIN")
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/categories/**")).hasAuthority("ADMIN")
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/categories/**")).hasAuthority("ADMIN")
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER", "USER")
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER")
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER")
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PATCH, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER")
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER")
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/orders/**")).hasAnyAuthority("USER", "SELLER", "ADMIN")
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/orders/**")).hasAuthority("USER")
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/orders/**")).hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/orders/**")).hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PATCH, "/api/orders/**")).hasAnyAuthority("USER", "ADMIN")
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}