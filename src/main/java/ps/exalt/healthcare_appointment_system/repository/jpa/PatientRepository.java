package ps.exalt.healthcare_appointment_system.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ps.exalt.healthcare_appointment_system.entity.Patient;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUser_Id(Long userId);

    List<Patient> findByUser_FirstNameContainingIgnoreCaseOrUser_LastNameContainingIgnoreCase(String firstName,
            String lastName);

    Optional<Patient> findByUser_Email(String email);

    boolean existsByUser_Id(Long userId);

    boolean existsByUser_Email(String email);
}
