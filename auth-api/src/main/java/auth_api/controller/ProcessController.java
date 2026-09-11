package auth_api.controller;

import auth_api.client.DataApiClient;
import auth_api.dto.ProcessRequest;
import auth_api.dto.TransformResponse;
import auth_api.entity.ProcessingLog;
import auth_api.entity.User;
import auth_api.repository.ProcessingLogRepository;
import auth_api.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ProcessController {

    private final DataApiClient dataApiClient;
    private final AuthService authService;
    private final ProcessingLogRepository processingLogRepository;

    public ProcessController(DataApiClient dataApiClient, AuthService authService, ProcessingLogRepository processingLogRepository) {
        this.dataApiClient = dataApiClient;
        this.authService = authService;
        this.processingLogRepository = processingLogRepository;
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/process")
    public TransformResponse process(@Valid @RequestBody ProcessRequest request,
                                     Authentication authentication) {

        String email = authentication.getName();
        User user = authService.findByEmail(email);
        UUID userId = user.getId();

        TransformResponse response = dataApiClient.transform(request);

        ProcessingLog log = new ProcessingLog(
                userId,
                request.text(),
                response.result(),
                LocalDateTime.now()
        );

        processingLogRepository.save(log);

        return response;
    }
}