package org.example.expert.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

public class TodoSearchResponse {
    private final String title;
    private final int countManager;
    private final int countComment;

    public TodoSearchResponse(String title, int countManager, int countComment) {
        this.title = title;
        this.countManager = countManager;
        this.countComment = countComment;
    }
}
