package ro.unibuc.hello.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import ro.unibuc.hello.request.AssignTodoRequest;
import ro.unibuc.hello.request.CreateTodoRequest;
import ro.unibuc.hello.request.EditTodoRequest;
import ro.unibuc.hello.response.TodoResponse;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.service.TodoService;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @GetMapping
    public List<TodoResponse> getTodosByUserEmail(@RequestParam String assigneeEmail) throws EntityNotFoundException {
        return todoService.getTodosByUserEmail(assigneeEmail);
    }

    @GetMapping("/{id}")
    public TodoResponse getTodoById(@PathVariable String id) throws EntityNotFoundException {
        return todoService.getTodoById(id);
    }

    @PostMapping
    public TodoResponse createTodo(@Valid @RequestBody CreateTodoRequest request) throws EntityNotFoundException {
        return todoService.createTodo(request);
    }

    @PatchMapping("/{id}/done")
    public TodoResponse setDone(@PathVariable String id, @RequestBody boolean done) throws EntityNotFoundException {
        return todoService.setDone(id, done);
    }

    @PatchMapping("/{id}/assignee")
    public TodoResponse assign(@PathVariable String id, @Valid @RequestBody AssignTodoRequest request) throws EntityNotFoundException {
        return todoService.assign(id, request);
    }

    @PatchMapping("/{id}/description")
    public TodoResponse edit(@PathVariable String id, @Valid @RequestBody EditTodoRequest request) throws EntityNotFoundException {
        return todoService.edit(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable String id) throws EntityNotFoundException {
        todoService.deleteTodo(id);
    }
}
