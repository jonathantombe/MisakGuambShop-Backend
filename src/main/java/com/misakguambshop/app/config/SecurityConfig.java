
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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/auth/signup/user").permitAll()
                                .requestMatchers("/api/auth/signup/seller").permitAll()
                                .requestMatchers("/api/sellers/**").hasAnyRole("ADMIN", "SELLER")
                                .requestMatchers("/api/users/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/categories/**").hasAnyAuthority("ADMIN", "SELLER", "USER")
                                .requestMatchers(HttpMethod.POST, "/api/categories/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAnyAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAnyAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyAuthority("ADMIN", "SELLER", "USER")
                                .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyAuthority("ADMIN", "SELLER")
                                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyAuthority("ADMIN", "SELLER")
                                .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasAnyAuthority("ADMIN", "SELLER")
                                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyAuthority("ADMIN", "SELLER")
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