package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository

public interface CustomTodoRepository{
    TodoResponse findByIdWithUser(Long todoId);
    Page<TodoSearchResponse> findByKeywordAndCreatedAtAndManagersName(String keyword,
                                                                      String managerKeyword,
                                                                      LocalDateTime startTime,
                                                                      LocalDateTime endTime,
                                                                      Pageable pageable);
}
