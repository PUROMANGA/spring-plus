package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;


public interface TodoRepository extends JpaRepository<Todo, Long>, CustomTodoRepository {

    @Query("SELECT t " +
            "FROM Todo t " +
            "LEFT JOIN FETCH t.user u " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    /**
     * 특정 weather을 받아서, 그 weather을 찾아서 paing과 함께 내주는 쿼리
     * @param weather
     * @param pageable
     * @return
     */

    @Query("SELECT t " +
            "FROM Todo t " +
            "INNER JOIN t.user u " +
            "WHERE t.weather = :weather")
    Page<Todo> findByWeather(String weather, Pageable pageable);

    /**
     * 시작일과 수정일 사이를 설정하면 그 사이의 값만 들고 오는 쿼리
     * @param startDate
     * @param endDate
     * @param pageable
     * @return
     */

    @Query("SELECT t " +
            "FROM Todo t " +
            "INNER JOIN t.user u " +
            "WHERE t.modifiedAt " +
            "between :startDate and :endDate " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByModifiedAtWhereStartDateAndEnddate(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT t " +
            "FROM Todo t " +
            "INNER JOIN t.user u " +
            "WHERE t.weather = :weather " +
            "and t.modifiedAt " +
            "between :startDate and :endDate " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByWeatherAndModifiedAtWhereStartDateAndEnddate(String weather, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    /**
     * 사용 안하는 메소드, 하지만 나중에 확인을 위해 조금 두도록 하겠습니다.
     */
//    @Query("SELECT t FROM Todo t " +
//            "LEFT JOIN t.user " +
//            "WHERE t.id = :todoId")
//    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

}
