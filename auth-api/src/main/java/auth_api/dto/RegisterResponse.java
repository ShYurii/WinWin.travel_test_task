package auth_api.dto;

import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String email
) {
}
