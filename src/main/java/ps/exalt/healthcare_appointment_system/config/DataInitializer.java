package ps.exalt.healthcare_appointment_system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ps.exalt.healthcare_appointment_system.entity.Doctor;
import ps.exalt.healthcare_appointment_system.entity.DoctorWorkingTimeSlot;
import ps.exalt.healthcare_appointment_system.entity.Role;
import ps.exalt.healthcare_appointment_system.entity.User;
import ps.exalt.healthcare_appointment_system.enums.Gender;
import ps.exalt.healthcare_appointment_system.enums.UserRole;
import ps.exalt.healthcare_appointment_system.exception.NotFoundException;
import ps.exalt.healthcare_appointment_system.repository.jpa.DoctorRepository;
import ps.exalt.healthcare_appointment_system.repository.jpa.DoctorWorkingTimeSlotRepository;
import ps.exalt.healthcare_appointment_system.repository.jpa.RoleRepository;
import ps.exalt.healthcare_appointment_system.repository.jpa.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorRepository doctorRepository;
    private final DoctorWorkingTimeSlotRepository doctorWorkingTimeSlotRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeUsers();
        initializeDefaultWorkingTimeSlots();
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

    private void initializeDefaultWorkingTimeSlots() {
        // Set default working time slots for all doctors who don't have time slots set
        List<Doctor> doctors = doctorRepository.findAll();

        for (Doctor doctor : doctors) {
            List<DoctorWorkingTimeSlot> existingSlots = doctorWorkingTimeSlotRepository
                    .findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(doctor.getId());

            if (existingSlots.isEmpty()) {
                // Create default working time slots (SUNDAY to THURSDAY, 9 AM to 5 PM)
                List<DoctorWorkingTimeSlot> defaultSlots = List.of(
                        createWorkingTimeSlot(doctor, DayOfWeek.SUNDAY),
                        createWorkingTimeSlot(doctor, DayOfWeek.MONDAY),
                        createWorkingTimeSlot(doctor, DayOfWeek.TUESDAY),
                        createWorkingTimeSlot(doctor, DayOfWeek.WEDNESDAY),
                        createWorkingTimeSlot(doctor, DayOfWeek.THURSDAY));

                doctorWorkingTimeSlotRepository.saveAll(defaultSlots);
            }
        }
    }

    private DoctorWorkingTimeSlot createWorkingTimeSlot(Doctor doctor, DayOfWeek dayOfWeek) {
        return DoctorWorkingTimeSlot.builder()
                .doctor(doctor)
                .dayOfWeek(dayOfWeek)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .isActive(true)
                .build();
    }
}
