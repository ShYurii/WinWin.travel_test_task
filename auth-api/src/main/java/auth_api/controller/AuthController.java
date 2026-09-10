package auth_api.controller;


import auth_api.dto.LoginRequest;
import auth_api.dto.RegisterRequest;
import auth_api.entity.User;
import auth_api.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody RegisterRequest request) {
        return authService.register(
                request.email(),
                request.password()
        );
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {
        return authService.login(
                request.email(),
                request.password()
        );
    }
}
