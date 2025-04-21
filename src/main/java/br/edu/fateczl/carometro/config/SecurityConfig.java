package br.edu.fateczl.carometro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                                .requestMatchers(
                                        "/",
                                        "/home",
                                        "/registro/**",
                                        "/postagens/publicas/**",
                                        "/postagens/foto/**",
                                        "/css/**",
                                        "/js/**",
                                        "/img/**",
                                        "/webjars/**",
                                        "/error"
                                ).permitAll()
                                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "COORDENADOR")
                                .requestMatchers("/admin/usuarios").hasRole("ADMIN")
                                .requestMatchers("/coordenador/**").hasRole("COORDENADOR")
                                .requestMatchers("/ex-aluno/**").hasRole("EX_ALUNO")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(customAuthenticationSuccessHandler())
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                        .logoutSuccessUrl("/login?logout")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("carometroAppRememberMeKey")
                        .userDetailsService(userDetailsService)
                );

        http.csrf(csrf -> csrf.ignoringRequestMatchers("/logout"));
        return http.build();
    }
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            Set<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            if (roles.contains("ROLE_ADMIN")) {
                response.sendRedirect("/admin/dashboard");
            } else if (roles.contains("ROLE_COORDENADOR")) {
                response.sendRedirect("/coordenador/dashboard");
            } else if (roles.contains("ROLE_EX_ALUNO")) {
                response.sendRedirect("/ex-aluno/dashboard");
            } else {
                response.sendRedirect("/");
            }
        };
    }

}
