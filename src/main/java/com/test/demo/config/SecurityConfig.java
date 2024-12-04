package com.test.demo.config;

import com.test.demo.config.jwt.JwtAuthenticationFilter;
import com.test.demo.config.jwt.JwtTokenProvider;
import com.test.demo.service.member.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailService customUserDetailService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (API 사용 시 필요)
                .authorizeRequests()
                .requestMatchers("/book/auth/**", "/login", "/oauth2/**", "/book/chat/rooms","/ws").permitAll() // 인증 없이 접근할 수 있는 경로
                .requestMatchers(HttpMethod.GET, "/book/member/me").authenticated() // 인증된 사용자만 접근 가능한 경로
                .requestMatchers(HttpMethod.GET, "/book/chat/rooms/{roomId}/messages").authenticated()
                .requestMatchers(HttpMethod.GET, "/book/chat/rooms/{roomId}").authenticated()
                .anyRequest().denyAll() // 나머지 경로는 모두 접근 거부
                .and()
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        // JWT 인증 필터 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // OAuth 2.0 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/api/member/me") // 로그인 성공 후 리다이렉트 URL 설정
                .failureUrl("/login?error=true")); // 로그인 실패 시 리다이렉트 URL 설정


        return http.build();  // http.build()로 반환
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        log.info("AuthenticationManager bean being created...");
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("http://52.78.186.21"); // React 앱의 URL
        configuration.addAllowedOriginPattern("ws://52.78.186.21");;
//        configuration.addAllowedOriginPattern("http://localhost");
//        configuration.addAllowedOriginPattern("ws://localhost");
        configuration.addAllowedMethod("*");  // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*");  // 모든 헤더 허용
        configuration.setAllowCredentials(true);  // 쿠키 및 인증 정보 포함 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 모든 경로에 CORS 설정 적용
        return source;
    }

}
