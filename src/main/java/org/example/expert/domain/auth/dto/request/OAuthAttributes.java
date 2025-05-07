package org.example.expert.domain.auth.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.example.expert.domain.auth.entity.UserOAuth;
import org.example.expert.domain.user.enums.UserRole;

import java.util.Map;

@Getter

public class OAuthAttributes {

    private Map<String, Object> attributes;
    private Number providerId;
    private String nameAttributeKey;
    private String email;
    private String url;
    private String name;
    private String location;
    private UserRole userRole;
    private String nickname;
    private String birthday;
    private String gender;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, Number providerId, String email, String url, String name, String location, UserRole userRole, String gender, String birthday, String nickname) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.providerId = providerId;
        this.email = email;
        this.url = url;
        this.name = name;
        this.location = location;
        this.userRole = userRole;
        this.gender = gender;
        this.birthday = birthday;
        this.nickname = nickname;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        switch (registrationId) {
            case "github" -> {
                return ofGithub(userNameAttributeName, attributes);
            }

            case "kakao" -> {
                return ofKakao(userNameAttributeName, attributes);
            }

            default -> throw new IllegalArgumentException("알 수 없는 접근자입니다" + registrationId);
        }
    }

    public static OAuthAttributes ofGithub(String userNameAttributeName, Map<String, Object> attributes) {

        return OAuthAttributes.builder()
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .providerId(((Number) attributes.get("id")).longValue())
                .email((String) attributes.get("email"))
                .url((String) attributes.get("url"))
                .name((String) attributes.get("name"))
                .location((String) attributes.get("location"))
                .userRole(UserRole.SOCIAL)
                .build();
    }

    public static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .providerId(((Number) attributes.get("id")).longValue())
                .email((String) kakaoAccount.get("nickname"))
                .name((String) profile.get("name"))
                .nickname((String) kakaoAccount.get("nickname"))
                .gender((String) kakaoAccount.get("gender"))
                .birthday((String) kakaoAccount.get("birthday"))
                .build();
    }

    public UserOAuth toEntity() {
        return UserOAuth.builder()
                .providerId(this.ID)
                .email(this.email)
                .url(this.url)
                .name(this.name)
                .location(this.location)
                .userRole(this.userRole)
                .oauthId(String.valueOf(this.attributes.get("id")))
                .oauthProvider("github")
                .build();
    }

    public UserOAuth toEntityForKakao() {
        return UserOAuth.builder()
                .providerId(this.ID)
                .email(this.email)
                .nickname(this.nickname)
                .name(this.name)
                .gender(this.gender)
                .birthday(this.birthday)
                .userRole(this.userRole)
                .oauthId(String.valueOf(this.attributes.get("id")))
                .oauthProvider("kakao")
                .build();
    }
}
