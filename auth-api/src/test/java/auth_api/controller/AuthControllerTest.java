package auth_api.controller;


import auth_api.dto.LoginResponse;
import auth_api.dto.RegisterResponse;
import auth_api.exception.InvalidCredentialsException;
import auth_api.exception.UserAlreadyExistsException;
import auth_api.service.AuthService;
import auth_api.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldRegisterUser() throws Exception {

        UUID userId = UUID.randomUUID();

        RegisterResponse response =
                new RegisterResponse(userId, "test@gmail.com");

        when(authService.register("test@gmail.com", "password"))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "test@gmail.com",
                                    "password": "password"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }

    @Test
    void shouldReturnConflictWhenUserAlreadyExists() throws Exception {

        when(authService.register("test@gmail.com", "password"))
                .thenThrow(new UserAlreadyExistsException(
                        "User with this email already exists"
                ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "test@gmail.com",
                                "password": "password"
                            }
                            """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("User with this email already exists"));
    }

    @Test
    void shouldRejectInvalidEmail() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "not-an-email",
                                "password": "password"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("must be a well-formed email address"));
    }

    @Test
    void shouldLoginUser() throws Exception {

        when(authService.login("test@gmail.com", "password"))
                .thenReturn(new LoginResponse("test-jwt-token"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "test@gmail.com",
                                "password": "password"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {

        when(authService.login("test@gmail.com", "wrong-password"))
                .thenThrow(new InvalidCredentialsException(
                        "Invalid email or password"
                ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "test@gmail.com",
                                "password": "wrong-password"
                            }
                            """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Invalid email or password"));
    }
}