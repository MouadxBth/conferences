package me.khadija.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Stream;

import static me.khadija.utilities.Utilities.ALGORITHM;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        log.info(request.getServletPath());
        if (request.getServletPath().equals("/login")
                || request.getServletPath().contains("/register")
                || request.getServletPath().equals("/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || authHeader.isBlank()) {
            log.info("EMPTY -----> " + request.getServletPath());
            log.error("AUTH HEADER NOT FOUND");
            response.setHeader("error", "Authorization header is missing or empty!)");
            response.sendError(FORBIDDEN.value());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            log.info(authHeader);
            final DecodedJWT decodedJWT = JWT.require(ALGORITHM)
                    .build()
                    .verify(authHeader);

            System.out.println(decodedJWT.getSubject() + " " + decodedJWT.getClaim("password").asString());

            final UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(decodedJWT.getSubject(),
                    null,
                    Stream.of(decodedJWT.getClaim("roles").asArray(String.class))
                            .map(SimpleGrantedAuthority::new).toList());

            SecurityContextHolder.getContext()
                    .setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        }
        catch (Exception e) {
            log.error("Authorization error {}", e.getMessage());
            response.setHeader("error", e.getMessage());
            response.sendError(FORBIDDEN.value());
        }
    }
}
