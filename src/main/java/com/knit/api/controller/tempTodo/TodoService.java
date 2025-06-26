package com.knit.api.controller.tempTodo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TodoService {

    private final TodoRepository todoRepository;

    // TODO 생성
    @Transactional
    public TodoDto.Response createTodo(TodoDto.CreateRequest request) {
        log.info("Creating new todo: {} by {}", request.getTitle(), request.getAuth());

        Todo todo = request.toEntity();
        Todo savedTodo = todoRepository.save(todo);

        log.info("Todo created successfully with id: {}", savedTodo.getId());
        return TodoDto.Response.from(savedTodo);
    }

    // 모든 TODO 조회
    public List<TodoDto.Summary> getAllTodos() {
        log.info("Fetching all todos");

        List<Todo> todos = todoRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return todos.stream()
                .map(TodoDto.Summary::from)
                .collect(Collectors.toList());
    }

    // 작성자별 TODO 조회
    public List<TodoDto.Summary> getTodosByAuth(String auth) {
        log.info("Fetching todos by auth: {}", auth);

        List<Todo> todos = todoRepository.findByAuthOrderByCreatedAtDesc(auth);
        return todos.stream()
                .map(TodoDto.Summary::from)
                .collect(Collectors.toList());
    }

    // 페이징 처리된 TODO 조회
    public Page<TodoDto.Summary> getTodos(int page, int size, String auth) {
        log.info("Fetching todos - page: {}, size: {}, auth: {}", page, size, auth);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Todo> todoPage;
        if (auth != null && !auth.trim().isEmpty()) {
            todoPage = todoRepository.findByAuth(auth, pageable);
        } else {
            todoPage = todoRepository.findAll(pageable);
        }

        return todoPage.map(TodoDto.Summary::from);
    }

    // TODO 상세 조회
    public TodoDto.Response getTodoById(Long id) {
        log.info("Fetching todo with id: {}", id);

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        return TodoDto.Response.from(todo);
    }

    // TODO 수정
    @Transactional
    public TodoDto.Response updateTodo(Long id, TodoDto.UpdateRequest request) {
        log.info("Updating todo with id: {}", id);

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        // 필드별 업데이트 (null이 아닌 경우만)
        if (request.getTitle() != null) {
            todo.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            todo.setDescription(request.getDescription());
        }
        if (request.getAuth() != null) {
            todo.setAuth(request.getAuth());
        }

        Todo updatedTodo = todoRepository.save(todo);
        log.info("Todo updated successfully with id: {}", updatedTodo.getId());

        return TodoDto.Response.from(updatedTodo);
    }

    // TODO 삭제
    @Transactional
    public void deleteTodo(Long id) {
        log.info("Deleting todo with id: {}", id);

        if (!todoRepository.existsById(id)) {
            throw new RuntimeException("Todo not found with id: " + id);
        }

        todoRepository.deleteById(id);
        log.info("Todo deleted successfully with id: {}", id);
    }

    // 전체 검색
    public List<TodoDto.Summary> searchTodos(String keyword) {
        log.info("Searching todos with keyword: {}", keyword);

        List<Todo> todos = todoRepository.searchByKeyword(keyword);
        return todos.stream()
                .map(TodoDto.Summary::from)
                .collect(Collectors.toList());
    }

    // 작성자별 검색
    public List<TodoDto.Summary> searchTodosByAuth(String auth, String keyword) {
        log.info("Searching todos by auth: {} with keyword: {}", auth, keyword);

        List<Todo> todos = todoRepository.searchByAuthAndKeyword(auth, keyword);
        return todos.stream()
                .map(TodoDto.Summary::from)
                .collect(Collectors.toList());
    }
}
