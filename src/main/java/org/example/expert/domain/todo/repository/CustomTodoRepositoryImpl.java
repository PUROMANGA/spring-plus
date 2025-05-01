package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.user.entity.QUser;

import java.util.Optional;

@RequiredArgsConstructor

public class CustomTodoRepositoryImpl implements CustomTodoRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public TodoResponse findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        TodoResponse content = jpaQueryFactory
                .select(Projections.constructor(TodoResponse.class,
                        todo.id,
                        todo.title,
                        todo.contents,
                        todo.weather,
                        user.id,
                        user.email,
                        todo.createdAt,
                        todo.modifiedAt))
                .from(todo)
                .leftJoin(todo.user, user)
                .where(todo.id.eq(todoId))
                .fetchOne();

        if (content == null) {
            throw new InvalidRequestException("Todo not found");
        }

        return content;
    }
}
