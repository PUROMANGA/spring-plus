package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor

public class CustomTodoRepositoryImpl implements CustomTodoRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QTodo todo = QTodo.todo;
    QManager manager = QManager.manager;
    QComment comment = QComment.comment;
    QUser user = QUser.user;

    @Override
    public TodoResponse findByIdWithUser(Long todoId) {

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

    @Override
    public Page<TodoSearchResponse> findByKeywordAndCreatedAtAndManagersName(String titleKeyword,
                                                                             String managerKeyword,
                                                                             LocalDateTime startTime,
                                                                             LocalDateTime endTime,
                                                                             Pageable pageable) {

        List<TodoSearchResponse> result = jpaQueryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.title,
                        manager.id.countDistinct(),
                        comment.id.countDistinct()
                        ))
                .from(todo)
                .innerJoin(todo.managers, manager)
                .innerJoin(todo.comments, comment)
                .where(containTodoTitle(titleKeyword),
                        betweenCreatedAt(startTime, endTime),
                        nickNameContain(managerKeyword)
                )
                .groupBy(todo.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(todo.countDistinct())
                .from(todo)
                .innerJoin(todo.managers, manager)
                .innerJoin(todo.comments, comment)
                .where(containTodoTitle(titleKeyword),
                        betweenCreatedAt(startTime, endTime),
                        nickNameContain(managerKeyword)
                )
                .fetchOne();

        return new PageImpl<>(result, pageable, total);
    }

    private BooleanBuilder containTodoTitle(String titleKeyword) {
        return nullSafeBooleanBuilder(() ->
            (titleKeyword == null || titleKeyword.isEmpty() ? null : todo.title.containsIgnoreCase(titleKeyword)));
    }

    private BooleanBuilder betweenCreatedAt(LocalDateTime startTime, LocalDateTime endTime) {
        return nullSafeBooleanBuilder(() -> (startTime == null || endTime == null) ? null : todo.createdAt.between(startTime, endTime));
    }

    private BooleanBuilder nickNameContain(String managerKeyword) {
        return nullSafeBooleanBuilder(() -> (managerKeyword == null || managerKeyword.isEmpty()) ? null : manager.user.nickname.containsIgnoreCase(managerKeyword));
    }

    private BooleanBuilder nullSafeBooleanBuilder(Supplier<BooleanExpression> supplier) {
        try {
            return new BooleanBuilder(supplier.get());
        } catch (Exception e) {
            return new BooleanBuilder();
        }
    }
}
