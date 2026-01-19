package com.ApiRestStock.CRUD.shared.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ApiRestStock.CRUD.shared.dto.AuthResponse;
import com.ApiRestStock.CRUD.shared.dto.LoginRequest;
import com.ApiRestStock.CRUD.shared.security.JwtService;
import com.ApiRestStock.CRUD.shared.service.AuthService;
import com.ApiRestStock.CRUD.shared.service.AuthService.LoginWithRefreshResult;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Value("${security.jwt.cookie-name:AUTH_TOKEN}")
    private String cookieName;

    @Value("${security.jwt.refresh-cookie-name:REFRESH_TOKEN}")
    private String refreshCookieName;

    @Value("${security.jwt.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${security.jwt.cookie.same-site:Lax}")
    private String cookieSameSite;

    @Value("${security.jwt.cookie.path:/}")
    private String cookiePath;

    @Value("${security.jwt.expiration-minutes:60}")
    private long jwtExpirationMinutes;

    @Value("${security.jwt.refresh-expiration-days:7}")
    private long jwtRefreshExpirationDays;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody LoginRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody  LoginRequest request) {
        LoginWithRefreshResult result = authService.loginWithRefresh(request);
        AuthResponse response = result.response();

        ResponseCookie accessCookie = ResponseCookie.from(cookieName, response.accessToken())
            .httpOnly(true)
            .secure(cookieSecure)
            .path(cookiePath)
            .sameSite(cookieSameSite)
            .maxAge(jwtExpirationMinutes * 60L)
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from(refreshCookieName, result.refreshToken())
            .httpOnly(true)
            .secure(cookieSecure)
            .path(cookiePath)
            .sameSite(cookieSameSite)
            .maxAge(jwtRefreshExpirationDays * 24L * 60L * 60L)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request) {
        String refreshToken = readCookie(request, refreshCookieName);
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Validación estricta del refresh token acá para evitar 500.
        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        LoginWithRefreshResult result = authService.refreshAccessToken(refreshToken);
        AuthResponse response = result.response();

        ResponseCookie accessCookie = ResponseCookie.from(cookieName, response.accessToken())
            .httpOnly(true)
            .secure(cookieSecure)
            .path(cookiePath)
            .sameSite(cookieSameSite)
            .maxAge(jwtExpirationMinutes * 60L)
            .build();

        ResponseCookie newRefreshCookie = ResponseCookie.from(refreshCookieName, result.refreshToken())
            .httpOnly(true)
            .secure(cookieSecure)
            .path(cookiePath)
            .sameSite(cookieSameSite)
            .maxAge(jwtRefreshExpirationDays * 24L * 60L * 60L)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, newRefreshCookie.toString())
            .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deletedAccessCookie = ResponseCookie.from(cookieName, "")
            .httpOnly(true)
            .secure(cookieSecure)
            .path(cookiePath)
            .sameSite(cookieSameSite)
            .maxAge(0)
            .build();

        ResponseCookie deletedRefreshCookie = ResponseCookie.from(refreshCookieName, "")
            .httpOnly(true)
            .secure(cookieSecure)
            .path(cookiePath)
            .sameSite(cookieSameSite)
            .maxAge(0)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, deletedAccessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, deletedRefreshCookie.toString())
            .build();
    }

    private String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie != null && name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    
}
