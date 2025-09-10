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
        List<Appointment> findByPatientId(Long patientId);

        List<Appointment> findByDoctorId(Long doctorId);

        List<Appointment> findByStatus(AppointmentStatus status);

        List<Appointment> findByAppointmentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

        List<Appointment> findByDoctorIdAndAppointmentDateBetween(Long doctorId, LocalDateTime startDate,
                        LocalDateTime endDate);

        // Check for overlapping appointments for a doctor (prevent double-booking)
        @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
                        "AND (a.status = 'SCHEDULED' OR a.status = 'IN_PROGRESS') " +
                        "AND ((a.startTime < :endTime AND a.endTime > :startTime))")
        List<Appointment> findOverlappingAppointments(@Param("doctorId") Long doctorId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);

        // Get all active appointments for a doctor on a specific date
        @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
                        "AND DATE(a.appointmentDate) = DATE(:date) " +
                        "AND (a.status = 'SCHEDULED' OR a.status = 'IN_PROGRESS') " +
                        "ORDER BY a.startTime")
        List<Appointment> findDoctorAppointmentsByDate(@Param("doctorId") Long doctorId,
                        @Param("date") LocalDateTime date);

        // Get patient's active appointments
        List<Appointment> findByPatientIdAndStatusInOrderByAppointmentDateAsc(Long patientId,
                        List<AppointmentStatus> statuses);

        // Get doctor's active appointments
        List<Appointment> findByDoctorIdAndStatusInOrderByAppointmentDateAsc(Long doctorId,
                        List<AppointmentStatus> statuses);
}
