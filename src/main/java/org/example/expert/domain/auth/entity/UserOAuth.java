package org.example.expert.domain.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.user.enums.UserRole;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UserOAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Number providerId;
    private String email;
    private String url;
    private String name;
    private String location;
    private UserRole userRole;
    private String oauthId;
    private String oauthProvider;
    private String nickname;
    private String birthday;
    private String gender;
}
