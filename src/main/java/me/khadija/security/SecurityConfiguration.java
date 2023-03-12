package me.khadija.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import me.khadija.services.UserService;
import me.khadija.views.login.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {


    private final UserService service;

    public SecurityConfiguration(UserService service) {
        this.service = service;
    }

    public AuthenticationFilter authenticationFilter() {
        final AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager());
        authenticationFilter.setFilterProcessesUrl("/login");
        return authenticationFilter;
    }

    public AuthorizationFilter authorizationFilter() {
        return new AuthorizationFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(service);
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        return new ProviderManager(authenticationProvider());
    }


    @Bean
    public CorsFilter corsFilter() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin",
                "Access-Control-Allow-Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "Origin, Accept",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource =
                new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests()
                .requestMatchers(new AntPathRequestMatcher("/images/*.png"))
                .permitAll();

        // Icons from the line-awesome addon
        http.authorizeHttpRequests()
                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg"))
                .permitAll();

        http.authenticationProvider(authenticationProvider());
//                .addFilter(authenticationFilter())
//                .addFilterBefore(authorizationFilter(), UsernamePasswordAuthenticationFilter.class)
//                .logout(logout -> {
//                    logout
//                            .logoutSuccessUrl("/homepage.html")
//                            .logoutUrl("api/v1/users/logout")
//                            .logoutSuccessUrl("/index.html")
//                            .invalidateHttpSession(true);
//                });
        super.configure(http);

//        setStatelessAuthentication(http,
//                new SecretKeySpec(Base64.getDecoder().decode("SECRET"), JWSAlgorithm.HS256.getName()),
//                "me.khadija.conferences");

        setLoginView(http, LoginView.class);
    }

}
