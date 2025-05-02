package org.example.expert.domain.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor

public class TodoSearchRequest {

    private String titleKeyword;
    private String managerKeyword;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
