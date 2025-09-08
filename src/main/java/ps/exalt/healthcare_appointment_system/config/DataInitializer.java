package ps.exalt.healthcare_appointment_system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ps.exalt.healthcare_appointment_system.entity.Role;
import ps.exalt.healthcare_appointment_system.entity.User;
import ps.exalt.healthcare_appointment_system.enums.Gender;
import ps.exalt.healthcare_appointment_system.enums.UserRole;
import ps.exalt.healthcare_appointment_system.exception.NotFoundException;
import ps.exalt.healthcare_appointment_system.repository.jpa.RoleRepository;
import ps.exalt.healthcare_appointment_system.repository.jpa.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeUsers();
    }

    private void initializeRoles() {
        for (UserRole userRole : UserRole.values()) {
            if (!roleRepository.existsByName(userRole)) {
                Role role = Role.builder()
                        .name(userRole)
                        .build();
                roleRepository.save(role);
            }
        }
    }

    private void initializeUsers() {
        // Create admin user
        if (!userRepository.existsByEmail("amr.zedan@admin.com")) {
            Role adminRole = roleRepository.findByName(UserRole.ADMIN)
                    .orElseThrow(() -> new NotFoundException("Admin role not found"));

            User admin = User.builder()
                    .firstName("Amr")
                    .lastName("Zedan")
                    .email("amr.zedan@admin.com")
                    .password(passwordEncoder.encode("amr123456"))
                    .phoneNumber("+970594334179")
                    .gender(Gender.MALE)
                    .address("Tulkarem, Palestine")
                    .role(adminRole)
                    .build();

            userRepository.save(admin);
        }
    }
}
