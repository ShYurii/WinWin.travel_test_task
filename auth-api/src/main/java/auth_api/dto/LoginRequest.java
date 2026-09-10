package auth_api.dto;

public record LoginRequest(
        String email,
        String password
) {
}