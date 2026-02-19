package ro.unibuc.hello.response;

public record TodoResponse(
    String id,
    String description,
    boolean done,
    String assigneeName,
    String assigneeEmail
) {}
