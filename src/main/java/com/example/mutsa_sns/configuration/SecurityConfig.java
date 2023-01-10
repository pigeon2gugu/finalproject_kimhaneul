package com.example.mutsa_sns.configuration;

import com.example.mutsa_sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
//WebSecurityConfigurerAdapter 는 이후 SpringBoot에서 잘 안쓰게 됨
public class SecurityConfig {

    private final UserService userService;

    @Value("${jwt.token.secret}")
    private String secretKey;

    private static final String[] PERMIT_URL_ARRAY = {
            /* swagger v2 */
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**",
            /* login and join*/
            "/api/v1/users/login",
            "/api/v1/users/join"

    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers(PERMIT_URL_ARRAY).permitAll()
                .antMatchers(HttpMethod.GET,"/api/v1/alarms").authenticated() // alarm 인가자만 허용
                .antMatchers(HttpMethod.POST,"/api/**").authenticated() // post는 인가자만 허용
                .antMatchers(HttpMethod.DELETE,"/api/**").authenticated() // delete는 인가자만 허용
                .antMatchers(HttpMethod.PUT,"/api/**").authenticated() // put는 인가자만 허용 (글 수정)
                .antMatchers("/api/v1/users/**/role/change").authenticated() // role 수정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt사용하는 경우 씀
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class) //UserNamePasswordAuthenticationFilter적용하기 전에 JWTTokenFilter를 적용
                .build();
    }

}