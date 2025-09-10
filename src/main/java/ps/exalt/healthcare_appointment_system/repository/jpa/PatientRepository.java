package ps.exalt.healthcare_appointment_system.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ps.exalt.healthcare_appointment_system.entity.Patient;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUserId(Long userId);

    List<Patient> findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCase(String firstName,
            String lastName);

    Optional<Patient> findByUserEmail(String email);

    boolean existsByUserId(Long userId);

    boolean existsByUserEmail(String email);
}
