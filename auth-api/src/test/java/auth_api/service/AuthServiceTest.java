package auth_api.service;


import auth_api.dto.LoginResponse;
import auth_api.dto.RegisterResponse;
import auth_api.entity.User;
import auth_api.exception.InvalidCredentialsException;
import auth_api.exception.UserAlreadyExistsException;
import auth_api.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUser() {

        UUID userId = UUID.randomUUID();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashed-password");

        User savedUser = new User(
                "test@gmail.com",
                "hashed-password"
        );

        // User.id private, поэтому задаём его через reflection
        org.springframework.test.util.ReflectionTestUtils.setField(
                savedUser,
                "id",
                userId
        );

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        RegisterResponse response =
                authService.register(
                        "test@gmail.com",
                        "password123"
                );

        assertEquals(userId, response.id());
        assertEquals("test@gmail.com", response.email());

        verify(userRepository)
                .findByEmail("test@gmail.com");

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void shouldRejectRegistrationWhenEmailAlreadyExists() {

        User existingUser = new User(
                "test@gmail.com",
                "hashed-password"
        );

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(
                        "test@gmail.com",
                        "password123"
                )
        );

        verify(userRepository)
                .findByEmail("test@gmail.com");

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(any());
    }

    @Test
    void shouldLoginUser() {

        UUID userId = UUID.randomUUID();

        User user = new User(
                "test@gmail.com",
                "hashed-password"
        );

        org.springframework.test.util.ReflectionTestUtils.setField(
                user,
                "id",
                userId
        );

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "hashed-password"
        )).thenReturn(true);

        when(jwtService.generateToken("test@gmail.com"))
                .thenReturn("jwt-token");

        LoginResponse response =
                authService.login(
                        "test@gmail.com",
                        "password123"
                );

        assertEquals("jwt-token", response.token());

        verify(userRepository)
                .findByEmail("test@gmail.com");

        verify(passwordEncoder)
                .matches("password123", "hashed-password");

        verify(jwtService)
                .generateToken("test@gmail.com");
    }


    @Test
    void shouldRejectLoginWithInvalidPassword() {

        User user = new User(
                "test@gmail.com",
                "hashed-password"
        );

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong-password",
                "hashed-password"
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(
                        "test@gmail.com",
                        "wrong-password"
                )
        );

        verify(userRepository)
                .findByEmail("test@gmail.com");

        verify(passwordEncoder)
                .matches(
                        "wrong-password",
                        "hashed-password"
                );

        verify(jwtService, never())
                .generateToken(any());
    }

    @Test
    void shouldRejectLoginWhenUserNotFound() {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(
                        "unknown@gmail.com",
                        "password123"
                )
        );

        verify(userRepository)
                .findByEmail("unknown@gmail.com");

        verify(passwordEncoder, never())
                .matches(any(), any());

        verify(jwtService, never())
                .generateToken(any());
    }

}