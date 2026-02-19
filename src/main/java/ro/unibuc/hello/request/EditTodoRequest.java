package ro.unibuc.hello.request;

import jakarta.validation.constraints.NotBlank;

public record EditTodoRequest(
    @NotBlank(message = "Description is required")
    String description
) {}
