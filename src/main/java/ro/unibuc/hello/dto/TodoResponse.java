package ro.unibuc.hello.dto;

public record TodoResponse(
    String id,
    String description,
    boolean done,
    String assigneeName,
    String assigneeEmail
) {}
