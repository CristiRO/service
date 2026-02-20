package ro.unibuc.hello.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateTodoRequest(
    @NotBlank(message = "Description is required")
    String description,

    @Email(message = "Invalid email format")
    @NotBlank(message = "Assignee email is required")
    String assigneeEmail
) {}
