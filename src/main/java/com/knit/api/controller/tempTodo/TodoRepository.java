package com.knit.api.controller.tempTodo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    // 작성자별 조회
    List<Todo> findByAuth(String auth);

    // 제목으로 검색 (대소문자 무시)
    List<Todo> findByTitleContainingIgnoreCase(String title);

    // 작성자별 검색
    List<Todo> findByAuthAndTitleContainingIgnoreCase(String auth, String title);

    // 페이징 처리된 조회 (작성자별)
    Page<Todo> findByAuth(String auth, Pageable pageable);

    // 최신순 조회 (작성자별)
    @Query("SELECT t FROM Todo t WHERE t.auth = :auth ORDER BY t.createdAt DESC")
    List<Todo> findByAuthOrderByCreatedAtDesc(@Param("auth") String auth);

    // 전체 검색 (제목 + 내용)
    @Query("SELECT t FROM Todo t WHERE t.title LIKE %:keyword% OR t.description LIKE %:keyword%")
    List<Todo> searchByKeyword(@Param("keyword") String keyword);

    // 작성자별 검색 (제목 + 내용)
    @Query("SELECT t FROM Todo t WHERE t.auth = :auth AND (t.title LIKE %:keyword% OR t.description LIKE %:keyword%)")
    List<Todo> searchByAuthAndKeyword(@Param("auth") String auth, @Param("keyword") String keyword);
}
