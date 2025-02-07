package api.dev.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private AuthenticationProvider authenticationProvider;

    

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }



    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // http.csrf(Customizer.withDefaults());// with default settings, which includes generating and returning a CSRF token to be used in forms or requests.
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(request -> request.requestMatchers("/api/auth/**").permitAll()
        .anyRequest().authenticated());
        
        http.sessionManagement( // ?
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)// dont store any infos about client in the server, dont create cookie
        );
        http.authenticationProvider(authenticationProvider);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
