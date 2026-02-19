package ro.unibuc.hello.response;

public record UserResponse(
    String id,
    String name,
    String email
) {}
