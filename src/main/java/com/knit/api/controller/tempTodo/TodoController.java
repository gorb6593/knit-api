//package com.knit.api.controller.tempTodo;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/todos")
//@RequiredArgsConstructor
//@Slf4j
//public class TodoController {
//
//    private final TodoService todoService;
//
//    // TODO 생성
//    @PostMapping
//    public ResponseEntity<TodoDto.Response> createTodo(@RequestBody TodoDto.CreateRequest request) {
//        log.info("POST /api/todos - Creating new todo");
//        TodoDto.Response response = todoService.createTodo(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    // 모든 TODO 조회
//    @GetMapping
//    public ResponseEntity<List<TodoDto.Summary>> getAllTodos() {
//        log.info("GET /api/todos - Fetching all todos");
//        List<TodoDto.Summary> todos = todoService.getAllTodos();
//        return ResponseEntity.ok(todos);
//    }
//
//    // 작성자별 TODO 조회
//    @GetMapping("/auth/{auth}")
//    public ResponseEntity<List<TodoDto.Summary>> getTodosByAuth(@PathVariable String auth) {
//        log.info("GET /api/todos/auth/{} - Fetching todos by auth", auth);
//        List<TodoDto.Summary> todos = todoService.getTodosByAuth(auth);
//        return ResponseEntity.ok(todos);
//    }
//
//    // 페이징 처리된 TODO 조회
//    @GetMapping("/page")
//    public ResponseEntity<Page<TodoDto.Summary>> getTodos(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) String auth) {
//        log.info("GET /api/todos/page - Fetching todos with pagination");
//        Page<TodoDto.Summary> todos = todoService.getTodos(page, size, auth);
//        return ResponseEntity.ok(todos);
//    }
//
//    // TODO 상세 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<TodoDto.Response> getTodoById(@PathVariable Long id) {
//        log.info("GET /api/todos/{} - Fetching todo details", id);
//        TodoDto.Response todo = todoService.getTodoById(id);
//        return ResponseEntity.ok(todo);
//    }
//
//    // TODO 수정
//    @PutMapping("/{id}")
//    public ResponseEntity<TodoDto.Response> updateTodo(
//            @PathVariable Long id,
//            @RequestBody TodoDto.UpdateRequest request) {
//        log.info("PUT /api/todos/{} - Updating todo", id);
//        TodoDto.Response response = todoService.updateTodo(id, request);
//        return ResponseEntity.ok(response);
//    }
//
//    // TODO 삭제
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
//        log.info("DELETE /api/todos/{} - Deleting todo", id);
//        todoService.deleteTodo(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    // 전체 검색
//    @GetMapping("/search")
//    public ResponseEntity<List<TodoDto.Summary>> searchTodos(@RequestParam String keyword) {
//        log.info("GET /api/todos/search - Searching todos with keyword: {}", keyword);
//        List<TodoDto.Summary> todos = todoService.searchTodos(keyword);
//        return ResponseEntity.ok(todos);
//    }
//
//    // 작성자별 검색
//    @GetMapping("/search/auth/{auth}")
//    public ResponseEntity<List<TodoDto.Summary>> searchTodosByAuth(
//            @PathVariable String auth,
//            @RequestParam String keyword) {
//        log.info("GET /api/todos/search/auth/{} - Searching todos by auth with keyword: {}", auth, keyword);
//        List<TodoDto.Summary> todos = todoService.searchTodosByAuth(auth, keyword);
//        return ResponseEntity.ok(todos);
//    }
//}
