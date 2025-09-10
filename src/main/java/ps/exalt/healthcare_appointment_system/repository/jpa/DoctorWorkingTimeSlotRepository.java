package ps.exalt.healthcare_appointment_system.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ps.exalt.healthcare_appointment_system.entity.DoctorWorkingTimeSlot;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface DoctorWorkingTimeSlotRepository extends JpaRepository<DoctorWorkingTimeSlot, Long> {

        /**
         * Find all time slots for a doctor on a specific day
         */
        List<DoctorWorkingTimeSlot> findByDoctorIdAndDayOfWeekOrderByStartTime(Long doctorId, DayOfWeek dayOfWeek);

        /**
         * Find all active time slots for a doctor on a specific day
         */
        List<DoctorWorkingTimeSlot> findByDoctorIdAndDayOfWeekAndIsActiveTrueOrderByStartTime(Long doctorId,
                        DayOfWeek dayOfWeek);

        /**
         * Find all time slots for a doctor
         */
        List<DoctorWorkingTimeSlot> findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(Long doctorId);

        /**
         * Find all active time slots for a doctor
         */
        List<DoctorWorkingTimeSlot> findByDoctorIdAndIsActiveTrueOrderByDayOfWeekAscStartTimeAsc(Long doctorId);

        /**
         * Delete all time slots for a doctor on a specific day
         */
        void deleteByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);

        /**
         * Check if doctor has any active slots on a specific day
         */
        boolean existsByDoctorIdAndDayOfWeekAndIsActiveTrue(Long doctorId, DayOfWeek dayOfWeek);

        /**
         * Find overlapping time slots for a doctor on a specific day
         */
        @Query("SELECT dwts FROM DoctorWorkingTimeSlot dwts WHERE dwts.doctor.id = :doctorId AND dwts.dayOfWeek = :dayOfWeek AND dwts.isActive = true AND dwts.startTime < :endTime AND dwts.endTime > :startTime")
        List<DoctorWorkingTimeSlot> findOverlappingSlots(@Param("doctorId") Long doctorId,
                        @Param("dayOfWeek") DayOfWeek dayOfWeek, @Param("startTime") java.time.LocalTime startTime,
                        @Param("endTime") java.time.LocalTime endTime);
}
