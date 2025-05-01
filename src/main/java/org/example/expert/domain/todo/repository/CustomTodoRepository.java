package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface CustomTodoRepository{
    TodoResponse findByIdWithUser(Long todoId);
}
