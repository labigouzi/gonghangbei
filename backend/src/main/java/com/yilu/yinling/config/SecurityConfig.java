package com.yilu.yinling.config;

import com.yilu.yinling.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthenticationFilter filter
    ) throws Exception {

        return http
                .csrf(c -> c.disable())

                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(e ->
                        e.authenticationEntryPoint(
                                (request, response, exception) ->
                                        response.sendError(401)
                        )
                )

                .authorizeHttpRequests(a -> a

                        // 公开接口
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/system/health",
                                "/doc.html",
                                "/v3/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()

                        // 管理员接口
                        .requestMatchers("/api/v1/admin/**")
                        .hasRole("ADMIN")

                        // 其他接口需要登录
                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        filter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}
