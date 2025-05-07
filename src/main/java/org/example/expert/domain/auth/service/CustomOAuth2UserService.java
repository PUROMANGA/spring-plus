package org.example.expert.domain.auth.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.auth.dto.request.OAuthAttributes;
import org.example.expert.domain.auth.entity.CustomOAuth2User;
import org.example.expert.domain.auth.entity.SessionUser;
import org.example.expert.domain.auth.entity.UserOAuth;
import org.example.expert.domain.auth.repository.UserOAuthRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor

public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserOAuthRepository userOAuthRepository;
    private final HttpSession httpSession;
    private static final String NAME_ATTRIBUTE = "login";
    private static final String EMAIL_KEY = "email";
    private final GitHubEmailFetcher emailFetcher;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        /// 사용자 정보
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        /// provider eky
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        /// 사용자의 자세한 정보
        final Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

        ///  식별자
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        ///  OAuthAttributes 생성자
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributes);

        switch (registrationId) {

            /// github일 때만
            case "github" -> {
                String primaryEmailAddress = extractPrimaryEmailAddress(oAuth2User, userRequest.getAccessToken().getTokenValue());

                if (primaryEmailAddress == null) {
                    return oAuth2User;
                }

                attributes.put(EMAIL_KEY, primaryEmailAddress);
                UserOAuth userOAuth = saveOAuthUserGithub(oAuthAttributes);
                httpSession.setAttribute("user", new SessionUser(userOAuth));
                return new CustomOAuth2User(oAuthAttributes);
            }

            ///  kakao일때만
            case "kakao" -> {

                UserOAuth userOAuth = saveOAuthUserForKakao(oAuthAttributes);
                httpSession.setAttribute("user", new SessionUser(userOAuth));
                return new CustomOAuth2User(oAuthAttributes);

            }
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth2 Provider: " + registrationId);
        }
    }

    private UserOAuth saveOAuthUserGithub(OAuthAttributes oAuthAttributes) {
        return userOAuthRepository.save(userOAuthRepository.findByEmail(oAuthAttributes.getEmail())
                .orElse(oAuthAttributes.toEntity()));
    }

    private UserOAuth saveOAuthUserForKakao(OAuthAttributes oAuthAttributes) {
        return userOAuthRepository.save(userOAuthRepository.findByEmail(oAuthAttributes.getEmail())
                .orElse(oAuthAttributes.toEntityForKakao()));
    }

    private String extractPrimaryEmailAddress(OAuth2User oAuth2User, String token) {
        String primaryEmailAddress = oAuth2User.getAttribute(EMAIL_KEY);

        if (!(primaryEmailAddress == null || primaryEmailAddress.isBlank())) {
            return primaryEmailAddress;
        }

        return emailFetcher.fetchPrimaryEmailAddress(token);
    }
}
