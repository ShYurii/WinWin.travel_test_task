package auth_api.service;


import auth_api.entity.User;
import auth_api.exception.UserAlreadyExistsException;
import auth_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String password) {

//        if (userRepository.findByEmail(email).isPresent()) {
//            throw new IllegalArgumentException("User with this email already exists");
//        }

        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("User with this email already exists");
                });

        String passwordHash = passwordEncoder.encode(password);

        User user = new User(email, passwordHash);

        return userRepository.save(user);
    }
}