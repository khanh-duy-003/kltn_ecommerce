package com.pk.core.identity.config;

import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.constant.UrlIdentConstant;
import com.pk.core.identity.security.handler.AccessDeniedHandlerImpl;
import com.pk.core.identity.security.handler.AuthEntryPoint;
import com.pk.core.identity.security.jwt.JwtPrincipalConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CorsConfigurationSource cors,
                                                   JwtPrincipalConverter jwtConverter,
                                                   AuthEntryPoint authEntryPoint,
                                                   AccessDeniedHandlerImpl accessDeniedHandler) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(cors))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(UrlIdentConstant.Auth.BASE + "/**").permitAll()
                        // Storefront đọc catalog: công khai, không cần đăng nhập (xem document/09-tong-hop-api-fe.md mục C)
                        .requestMatchers(HttpMethod.GET, UrlConstant.Product.BASE + "/**").permitAll()
                        // AI stylist/set-builder, badge: công khai theo spec FE (mục G/H). Checkout quote (mục D)
                        // spec ghi "Không cần" nhưng thiết kế ở đây dùng addressId đã lưu (giống OrderRest.create),
                        // không hỗ trợ khách vãng lai gửi địa chỉ rời rạc - CỐ Ý bắt buộc đăng nhập (rơi vào
                        // anyRequest().authenticated() mặc định bên dưới, không thêm permitAll ở đây), khác spec.
                        .requestMatchers(HttpMethod.POST, UrlConstant.Ai.BASE + "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, UrlConstant.Badge.BASE).permitAll()
                        // Webhook cổng thanh toán: không có Bearer token, tự xác thực bằng chữ ký (mục F)
                        .requestMatchers(HttpMethod.POST, UrlConstant.Payment.BASE + "/**").permitAll()
                        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        // Admin (Rest ở pk-api gói api.rest.admin, hằng route UrlAdminConstant đặt ở
                        // pk-model, gói constant.admin - cùng chỗ UrlConstant/UrlIdentConstant - xem
                        // RULE-CODE.md mục "Admin tách riêng: constant đặt ở pk-model", 2026-09-29,
                        // sửa lần 3 trong ngày): route login công khai (chưa có token lúc gọi), MỌI
                        // route admin còn lại bắt buộc role ADMIN. pk-identity vốn đã phụ thuộc pk-model
                        // nên gọi thẳng UrlAdminConstant được, không cần constant trung gian. Đặt SAU
                        // rule /actuator/** cụ thể hơn nhưng TRƯỚC anyRequest() mặc định, đúng thứ tự
                        // matcher cụ thể -> rộng dần của Spring Security.
                        .requestMatchers(HttpMethod.POST, UrlAdminConstant.Auth.LOGIN).permitAll()
                        .requestMatchers(UrlAdminConstant.Common.BASE + "/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));
        return http.build();
    }
}
