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
                .authorizeHttpRequests(auth -> {
                    //rutas autenticación
                    auth.requestMatchers(mvcMatcherBuilder.pattern("/api/auth/**")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern("/api/auth/signup")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern("/api/auth/signup/seller")).permitAll()

                            //Users
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/users")).hasAuthority("ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/users/{id}")).hasAnyAuthority("ADMIN", "USER")
                            .requestMatchers(mvcMatcherBuilder.pattern("/api/users/reset-password")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/users/request-reactivation")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/users/forgot-password")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/users/{id}/profile-image")).hasAnyAuthority("ADMIN","USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/users/reset-password")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/users/{id}")).hasAnyAuthority("ADMIN","USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/users/reactivate")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/users/{id}/profile-image")).hasAnyAuthority("ADMIN","USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PATCH, "/api/users/{id}/deactivate")).hasAnyAuthority("ADMIN", "USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PATCH, "/api/users/{id}/become-seller")).hasAuthority("USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/users/{id}")).hasAnyAuthority("ADMIN", "USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/users/{id}/profile-image")).hasAnyAuthority("ADMIN", "USER")

                            //gestión  categorías
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/categories/**")).hasAnyAuthority("ADMIN", "SELLER", "USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/categories/**")).hasAuthority("ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/categories/**")).hasAuthority("ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/categories/**")).hasAuthority("ADMIN")

                            //productos
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER", "USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/products/pending")).hasAuthority("ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/products/my-approved")).hasAnyAuthority("SELLER", "USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PATCH, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER")

                            //pedidos
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/orders/**")).hasAnyAuthority("USER", "SELLER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/orders/**")).hasAuthority("USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/orders/**")).hasAnyAuthority("USER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PATCH, "/api/orders/**")).hasAnyAuthority("USER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/orders/**")).hasAnyAuthority("USER", "ADMIN")


                            //envíos
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/shipments/**")).hasAnyAuthority("USER", "SELLER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/shipments/**")).hasAnyAuthority("USER", "SELLER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/shipments/**")).hasAnyAuthority("USER", "SELLER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PATCH, "/api/shipments/**")).hasAnyAuthority("ADMIN", "SELLER", "USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/shipments/**")).hasAnyAuthority("ADMIN", "SELLER", "USER")


                            // detalles pedidos
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/order-details/**")).hasAnyAuthority("USER", "SELLER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/order-details/**")).hasAuthority("USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/order-details/**")).hasAnyAuthority("USER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PATCH, "/api/order-details/**")).hasAnyAuthority("USER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/order-details/**")).hasAnyAuthority("USER", "ADMIN")



                            .anyRequest().authenticated();
                });


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
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}