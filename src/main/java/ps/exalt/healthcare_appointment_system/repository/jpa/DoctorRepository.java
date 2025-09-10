package ps.exalt.healthcare_appointment_system.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ps.exalt.healthcare_appointment_system.entity.Doctor;
import ps.exalt.healthcare_appointment_system.enums.DoctorStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);

    List<Doctor> findBySpecialization(String specialization);

    List<Doctor> findByStatus(DoctorStatus status);

    List<Doctor> findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCase(String firstName,
            String lastName);
}
