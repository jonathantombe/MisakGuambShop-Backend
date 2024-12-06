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
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;


import java.util.Arrays;

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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");  // Permite cualquier origen
        configuration.addAllowedMethod("*");         // Permite todos los métodos HTTP
        configuration.addAllowedHeader("*");         // Permite todos los headers
        configuration.setAllowCredentials(true);     // Permite credenciales

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/categories/list")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/categories/{id}")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/categories/name/{name}")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/categories/{categoryId}/products")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/categories/**")).hasAuthority("ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/categories/{id}")).hasAuthority("ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/categories/{id}")).hasAuthority("ADMIN")

                            //productos
                            .requestMatchers(mvcMatcherBuilder.pattern("/api/products/approved")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern("/api/products/detail/{id}")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern("/api/products/available")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern("/api/products/{id}/sales")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern("/api/products/search")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern("/api/products/search/{query}")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER", "USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/products/pending")).hasAuthority("ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/products/my-approved")).hasAnyAuthority("SELLER", "USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/products/{id}/disable")).hasAnyAuthority("SELLER", "USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/products/{id}/enable")).hasAnyAuthority("SELLER", "USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PATCH, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/products/**")).hasAnyAuthority("ADMIN", "SELLER")

                            //pedido
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/orders/**")).hasAnyAuthority("USER", "SELLER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/orders/**")).hasAuthority("USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/orders/my-orders")).hasAuthority("USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/orders/**")).hasAnyAuthority("USER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PATCH, "/api/orders/**")).hasAnyAuthority("USER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/orders/**")).hasAnyAuthority("USER", "ADMIN")

                            //envíos
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/shipments/**")).hasAnyAuthority("USER", "SELLER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/shipments/**")).hasAuthority("USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/shipments/**")).hasAnyAuthority("USER", "SELLER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PATCH, "/api/shipments/**")).hasAnyAuthority("ADMIN", "SELLER", "USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/shipments/**")).hasAnyAuthority("ADMIN", "SELLER", "USER")

                            //pagos
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/payments/{transactionId}")).permitAll()
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/payments")).hasAnyAuthority("USER", "SELLER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/payments/webhook")).hasAuthority("USER")

                            //UserPaymentMethod
                            .requestMatchers(HttpMethod.GET, "/api/user-payment-methods").hasAuthority("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/user-payment-methods/{id}").hasAnyAuthority("USER", "SELLER")
                            .requestMatchers(HttpMethod.GET, "/api/user-payment-methods/user/{userId}").hasAnyAuthority("USER", "SELLER")
                            .requestMatchers(HttpMethod.POST, "/api/user-payment-methods").hasAuthority("USER")
                            .requestMatchers(HttpMethod.PUT, "/api/user-payment-methods/{id}").hasAnyAuthority("USER", "ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/user-payment-methods/{id}").hasAuthority("ADMIN")

                            // detalles pedidos
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/order-details/**")).hasAnyAuthority("USER", "SELLER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/order-details/**")).hasAuthority("USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/order-details/**")).hasAnyAuthority("USER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PATCH, "/api/order-details/**")).hasAnyAuthority("USER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/order-details/**")).hasAnyAuthority("USER", "ADMIN")

                            //reviews
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/reviews/rate-product")).hasAnyAuthority("USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/api/reviews/**")).hasAnyAuthority("USER", "SELLER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/api/reviews/**")).hasAnyAuthority("USER")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, "/api/reviews/**")).hasAnyAuthority("USER", "ADMIN")
                            .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, "/api/reviews/**")).hasAnyAuthority("USER", "ADMIN")

                            .anyRequest().authenticated();
                });

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}