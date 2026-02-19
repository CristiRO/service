package ro.unibuc.hello.request;

public record CreateTodoRequest(String description, String assigneeEmail) {}
