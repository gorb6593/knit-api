package com.knit.api.controller.tempTodo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class TodoDto {

    // 생성 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private String title;
        private String description;
        private String auth;

        public Todo toEntity() {
            return Todo.builder()
                    .title(title)
                    .description(description)
                    .auth(auth)
                    .build();
        }
    }

    // 수정 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String title;
        private String description;
        private String auth;
    }

    // 응답 DTO (상세 정보 포함)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String auth;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;

        public static Response from(Todo todo) {
            return Response.builder()
                    .id(todo.getId())
                    .title(todo.getTitle())
                    .description(todo.getDescription())
                    .auth(todo.getAuth())
                    .createdAt(todo.getCreatedAt())
                    .updatedAt(todo.getUpdatedAt())
                    .build();
        }
    }

    // 목록 조회용 간단한 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private Long id;
        private String title;
        private String auth;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        public static Summary from(Todo todo) {
            return Summary.builder()
                    .id(todo.getId())
                    .title(todo.getTitle())
                    .auth(todo.getAuth())
                    .createdAt(todo.getCreatedAt())
                    .build();
        }
    }
}

