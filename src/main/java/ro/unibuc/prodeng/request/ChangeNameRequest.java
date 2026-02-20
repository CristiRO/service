package ro.unibuc.hello.request;

import jakarta.validation.constraints.NotBlank;

public record ChangeNameRequest(
    @NotBlank(message = "Name is required")
    String name
) {}
