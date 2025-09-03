package ps.exalt.healthcare_appointment_system.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ps.exalt.healthcare_appointment_system.entity.Appointment;
import ps.exalt.healthcare_appointment_system.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
        List<Appointment> findByPatient_Id(Long patientId);

        List<Appointment> findByDoctor_Id(Long doctorId);

        List<Appointment> findByStatus(AppointmentStatus status);

        List<Appointment> findByAppointmentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

        @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate BETWEEN :startDate AND :endDate")
        List<Appointment> findByDoctorAndDateRange(@Param("doctorId") Long doctorId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}
