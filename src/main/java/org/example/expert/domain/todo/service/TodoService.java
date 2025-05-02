package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }


    @Transactional(readOnly = true)
    public Page<TodoResponse> getTodos(int page, int size,
                                       String weather,
                                       LocalDateTime startDate,
                                       LocalDateTime endDate) {
        Pageable pageable = PageRequest.of(page - 1, size);

        /**
         * READ ME 참조
         */

        Page<Todo> todos;
        if (weather != null && startDate != null && endDate != null) {
            todos = todoRepository.findByWeatherAndModifiedAtWhereStartDateAndEnddate(weather, startDate, endDate, pageable);
        } else if(startDate != null && endDate != null) {
            todos = todoRepository.findByModifiedAtWhereStartDateAndEnddate(startDate, endDate, pageable);
        } else if(weather != null) {
            todos = todoRepository.findByWeather(weather, pageable);
        } else if((startDate == null) ^ (endDate == null)) {
            throw new RuntimeException("수정일 시작과 수정일 끝을 둘 다 입력해주세요");
        } else {
            todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);
        }

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    @Transactional(readOnly = true)
    public TodoResponse getTodo(long todoId) {
        return todoRepository.findByIdWithUser(todoId);
    }

    @Transactional(readOnly = true)
    public Page<TodoSearchResponse> getTodoKeywordService(String titleKeyword,
                                                          String managerKeyword,
                                                          LocalDateTime startTime,
                                                          LocalDateTime endTime,
                                                          Pageable pageable) {
        return todoRepository.findByKeywordAndCreatedAtAndManagersName(titleKeyword, managerKeyword, startTime, endTime, pageable);
    }
}
