package org.example.expert.domain.log;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "log")

public class Log extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String method;
    private String uri;

    public Log(Long userId, String method, String uri) {
        this.userId = userId;
        this.method = method;
        this.uri = uri;
    }
}
