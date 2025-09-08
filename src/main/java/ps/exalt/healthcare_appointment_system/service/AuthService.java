package ps.exalt.healthcare_appointment_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ps.exalt.healthcare_appointment_system.config.JwtUtil;
import ps.exalt.healthcare_appointment_system.dto.request.LoginRequest;
import ps.exalt.healthcare_appointment_system.dto.response.LoginResponse;
import ps.exalt.healthcare_appointment_system.entity.User;
import ps.exalt.healthcare_appointment_system.exception.InvalidException;
import ps.exalt.healthcare_appointment_system.exception.NotFoundException;
import ps.exalt.healthcare_appointment_system.repository.jpa.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidException("Invalid email or password");
        }

        // Update last login timestamp
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().getName().name());

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().getName().name(),
                "Login successful");
    }
}
