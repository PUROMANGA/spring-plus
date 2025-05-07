package org.example.expert.domain.auth.entity;

import lombok.Getter;

import java.io.Serializable;

@Getter

public class SessionUser implements Serializable {

    private final Number githubId;
    private final String email;
    private final String name;

    public SessionUser(UserOAuth userOAuth) {
        this.githubId = userOAuth.getId();
        this.email = userOAuth.getEmail();
        this.name = userOAuth.getName();
    }
}
