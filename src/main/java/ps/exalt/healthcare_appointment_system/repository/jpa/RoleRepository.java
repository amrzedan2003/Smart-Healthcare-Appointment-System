package ps.exalt.healthcare_appointment_system.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ps.exalt.healthcare_appointment_system.entity.Role;
import ps.exalt.healthcare_appointment_system.enums.UserRole;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(UserRole name);

    boolean existsByName(UserRole name);
}
