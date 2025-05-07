package org.example.expert.domain.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.auth.dto.request.OAuthAttributes;
import org.example.expert.domain.auth.entity.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor

public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        OAuthAttributes oAuthAttributes = customOAuth2User.getOAuthAttributes();

        String email = oAuthAttributes.getEmail();

        if (email == null) {
            throw new RuntimeException("이메일 정보를 가져올 수 없습니다.");
        }

        String token = jwtUtil.createToken(oAuthAttributes.getProviderId().longValue(), oAuthAttributes.getEmail(), oAuthAttributes.getName(), oAuthAttributes.getUserRole());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("{\"token\":\"" + token + "\"}");
    }
}
