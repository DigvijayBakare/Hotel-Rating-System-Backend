package com.gateway.controller;

import com.gateway.entities.AuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @GetMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient client,
            @AuthenticationPrincipal OidcUser user, Model model
    ) {
        log.info("user email id: {}", user.getEmail());

        // creating auth response object
        AuthResponse authResponse = new AuthResponse();
        authResponse.setUserId(user.getEmail());        // setting userId
        authResponse.setAccessToken(client.getAccessToken().getTokenValue());       // setting token
        authResponse.setRefreshToken(client.getRefreshToken().getTokenValue());     // setting refresh token
        authResponse.setExpireAt(client.getAccessToken().getExpiresAt().getEpochSecond());     // setting expire time

        List<String> authorities = user.getAuthorities().stream().map(grantedAuthority -> {
            return grantedAuthority.getAuthority();
        }).collect(Collectors.toList());

        authResponse.setAuthorities(authorities);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}


// http://localhost:8084/login/oauth2/code/okta
// This is our redirecting url