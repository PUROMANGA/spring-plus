package org.example.expert.domain.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.auth.dto.request.OAuthAttributes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter

public class CustomOAuth2User implements OAuth2User {

    private final OAuthAttributes oAuthAttributes;

    @Override
    public Map<String, Object> getAttributes() {
        return oAuthAttributes.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + oAuthAttributes.getUserRole().name()));
    }

    @Override
    public String getName() {
        if(oAuthAttributes.getName() != null) {
            return oAuthAttributes.getName();
        } else if (oAuthAttributes.getEmail() != null) {
            return oAuthAttributes.getEmail();
        }
        return String.valueOf(oAuthAttributes.getID());
    }

    public OAuthAttributes getOAuthAttributes() {
        return oAuthAttributes;
    }
}
