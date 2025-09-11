package ps.exalt.healthcare_appointment_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.exalt.healthcare_appointment_system.dto.request.DoctorMultipleTimeSlotsRequest;
import ps.exalt.healthcare_appointment_system.dto.request.TimeSlotRequest;
import ps.exalt.healthcare_appointment_system.dto.response.DoctorMultipleTimeSlotsResponse;
import ps.exalt.healthcare_appointment_system.dto.response.TimeSlotResponse;
import ps.exalt.healthcare_appointment_system.entity.Doctor;
import ps.exalt.healthcare_appointment_system.entity.DoctorWorkingTimeSlot;
import ps.exalt.healthcare_appointment_system.exception.InvalidException;
import ps.exalt.healthcare_appointment_system.exception.NotFoundException;
import ps.exalt.healthcare_appointment_system.repository.jpa.DoctorRepository;
import ps.exalt.healthcare_appointment_system.repository.jpa.DoctorWorkingTimeSlotRepository;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorWorkingTimeSlotsService {
        private final DoctorWorkingTimeSlotRepository timeSlotRepository;
        private final DoctorRepository doctorRepository;

        public DoctorMultipleTimeSlotsResponse setDoctorTimeSlots(DoctorMultipleTimeSlotsRequest request,
                        String doctorEmail) {
                // Get doctor from authenticated email
                Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                                .orElseThrow(() -> new NotFoundException("Doctor not found"));

                // Validate time slots
                validateTimeSlots(request.getTimeSlots());

                // Delete existing time slots for this day
                timeSlotRepository.deleteByDoctorIdAndDayOfWeek(doctor.getId(), request.getDayOfWeek());

                // Create new time slots
                List<DoctorWorkingTimeSlot> timeSlots = new ArrayList<>();
                for (TimeSlotRequest slotRequest : request.getTimeSlots()) {
                        DoctorWorkingTimeSlot timeSlot = DoctorWorkingTimeSlot.builder()
                                        .doctor(doctor)
                                        .dayOfWeek(request.getDayOfWeek())
                                        .startTime(slotRequest.getStartTime())
                                        .endTime(slotRequest.getEndTime())
                                        .isActive(slotRequest.getIsActive())
                                        .build();
                        timeSlots.add(timeSlot);
                }

                // Save all time slots
                List<DoctorWorkingTimeSlot> savedSlots = timeSlotRepository.saveAll(timeSlots);

                return convertToResponse(doctor.getId(), request.getDayOfWeek(), savedSlots);
        }

        public DoctorMultipleTimeSlotsResponse addTimeSlot(DayOfWeek dayOfWeek, TimeSlotRequest slotRequest,
                        String doctorEmail) {
                // Get doctor from authenticated email
                Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                                .orElseThrow(() -> new NotFoundException("Doctor not found"));

                // Validate the new time slot
                validateSingleTimeSlot(slotRequest);

                // Check for overlapping slots
                List<DoctorWorkingTimeSlot> overlapping = timeSlotRepository.findOverlappingSlots(
                                doctor.getId(), dayOfWeek, slotRequest.getStartTime(), slotRequest.getEndTime());

                if (!overlapping.isEmpty()) {
                        throw new InvalidException(
                                        String.format("Time slot %s - %s overlaps with existing working hours on %s",
                                                        slotRequest.getStartTime(), slotRequest.getEndTime(),
                                                        dayOfWeek));
                }

                // Create new time slot
                DoctorWorkingTimeSlot timeSlot = DoctorWorkingTimeSlot.builder()
                                .doctor(doctor)
                                .dayOfWeek(dayOfWeek)
                                .startTime(slotRequest.getStartTime())
                                .endTime(slotRequest.getEndTime())
                                .isActive(slotRequest.getIsActive())
                                .build();

                timeSlotRepository.save(timeSlot);

                // Return all slots for this day
                return getDoctorTimeSlots(dayOfWeek, doctorEmail);
        }

        @Transactional(readOnly = true)
        public DoctorMultipleTimeSlotsResponse getDoctorTimeSlots(DayOfWeek dayOfWeek, String doctorEmail) {
                // Get doctor from authenticated email
                Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                                .orElseThrow(() -> new NotFoundException("Doctor not found"));

                List<DoctorWorkingTimeSlot> timeSlots = timeSlotRepository
                                .findByDoctorIdAndDayOfWeekOrderByStartTime(doctor.getId(), dayOfWeek);

                return convertToResponse(doctor.getId(), dayOfWeek, timeSlots);
        }

        @Transactional(readOnly = true)
        public List<DoctorMultipleTimeSlotsResponse> getAllDoctorTimeSlots(String doctorEmail) {
                // Get doctor from authenticated email
                Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                                .orElseThrow(() -> new NotFoundException("Doctor not found"));

                List<DoctorWorkingTimeSlot> allSlots = timeSlotRepository
                                .findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(doctor.getId());

                // Group by day of week
                return allSlots.stream()
                                .collect(Collectors.groupingBy(DoctorWorkingTimeSlot::getDayOfWeek))
                                .entrySet().stream()
                                .map(entry -> convertToResponse(doctor.getId(), entry.getKey(), entry.getValue()))
                                .sorted(Comparator.comparing(DoctorMultipleTimeSlotsResponse::getDayOfWeek))
                                .collect(Collectors.toList());
        }

        /**
         * Get only active time slots for a doctor on a specific day
         */
        @Transactional(readOnly = true)
        public DoctorMultipleTimeSlotsResponse getActiveDoctorTimeSlots(DayOfWeek dayOfWeek, String doctorEmail) {
                // Get doctor from authenticated email
                Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                                .orElseThrow(() -> new NotFoundException("Doctor not found"));

                List<DoctorWorkingTimeSlot> activeSlots = timeSlotRepository
                                .findByDoctorIdAndDayOfWeekAndIsActiveTrueOrderByStartTime(doctor.getId(), dayOfWeek);

                return convertToResponse(doctor.getId(), dayOfWeek, activeSlots);
        }

        @Transactional(readOnly = true)
        public List<DoctorMultipleTimeSlotsResponse> getAllActiveDoctorTimeSlots(String doctorEmail) {
                // Get doctor from authenticated email
                Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                                .orElseThrow(() -> new NotFoundException("Doctor not found"));

                List<DoctorWorkingTimeSlot> activeSlots = timeSlotRepository
                                .findByDoctorIdAndIsActiveTrueOrderByDayOfWeekAscStartTimeAsc(doctor.getId());

                // Group by day of week
                return activeSlots.stream()
                                .collect(Collectors.groupingBy(DoctorWorkingTimeSlot::getDayOfWeek))
                                .entrySet().stream()
                                .map(entry -> convertToResponse(doctor.getId(), entry.getKey(), entry.getValue()))
                                .sorted(Comparator.comparing(DoctorMultipleTimeSlotsResponse::getDayOfWeek))
                                .collect(Collectors.toList());
        }

        public void deleteTimeSlot(Long slotId, String doctorEmail) {
                DoctorWorkingTimeSlot slot = timeSlotRepository.findById(slotId)
                                .orElseThrow(() -> new NotFoundException("Time slot not found with ID: " + slotId));

                // Get doctor from authenticated email
                Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                                .orElseThrow(() -> new NotFoundException("Doctor not found"));

                // Verify this time slot belongs to the authenticated doctor
                if (!slot.getDoctor().getId().equals(doctor.getId())) {
                        throw new InvalidException("You can only delete your own time slots");
                }

                timeSlotRepository.delete(slot);
        }

        /**
         * Delete all time slots for a doctor on a specific day
         */
        public void deleteDoctorTimeSlots(DayOfWeek dayOfWeek, String doctorEmail) {
                // Get doctor from authenticated email
                Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                                .orElseThrow(() -> new NotFoundException("Doctor not found"));

                timeSlotRepository.deleteByDoctorIdAndDayOfWeek(doctor.getId(), dayOfWeek);
        }

        public TimeSlotResponse updateTimeSlot(Long slotId, TimeSlotRequest request, String doctorEmail) {
                DoctorWorkingTimeSlot slot = timeSlotRepository.findById(slotId)
                                .orElseThrow(() -> new NotFoundException("Time slot not found with ID: " + slotId));

                // Get doctor from authenticated email
                Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                                .orElseThrow(() -> new NotFoundException("Doctor not found"));

                // Verify this time slot belongs to the authenticated doctor
                if (!slot.getDoctor().getId().equals(doctor.getId())) {
                        throw new InvalidException("You can only update your own time slots");
                }

                // Validate the updated time slot
                validateSingleTimeSlot(request);

                // Check for overlapping slots (excluding the current slot)
                List<DoctorWorkingTimeSlot> overlapping = timeSlotRepository.findOverlappingSlots(
                                slot.getDoctor().getId(), slot.getDayOfWeek(), request.getStartTime(),
                                request.getEndTime())
                                .stream()
                                .filter(existingSlot -> !existingSlot.getId().equals(slotId))
                                .collect(Collectors.toList());

                if (!overlapping.isEmpty()) {
                        throw new InvalidException(
                                        String.format("Updated time slot %s - %s would overlap with existing working hours on %s",
                                                        request.getStartTime(), request.getEndTime(),
                                                        slot.getDayOfWeek()));
                }

                // Update the slot
                slot.setStartTime(request.getStartTime());
                slot.setEndTime(request.getEndTime());
                slot.setIsActive(request.getIsActive());

                DoctorWorkingTimeSlot savedSlot = timeSlotRepository.save(slot);
                return convertSlotToResponse(savedSlot);
        }

        private void validateTimeSlots(List<TimeSlotRequest> timeSlots) {
                if (timeSlots == null || timeSlots.isEmpty()) {
                        throw new InvalidException("At least one time slot is required");
                }

                // Validate each slot
                for (TimeSlotRequest slot : timeSlots) {
                        validateSingleTimeSlot(slot);
                }

                // Check for overlapping slots within the request
                for (int i = 0; i < timeSlots.size(); i++) {
                        for (int j = i + 1; j < timeSlots.size(); j++) {
                                TimeSlotRequest slot1 = timeSlots.get(i);
                                TimeSlotRequest slot2 = timeSlots.get(j);

                                if (slot1.getStartTime().isBefore(slot2.getEndTime()) &&
                                                slot1.getEndTime().isAfter(slot2.getStartTime())) {
                                        throw new InvalidException(
                                                        String.format("Time slots overlap: %s-%s and %s-%s",
                                                                        slot1.getStartTime(), slot1.getEndTime(),
                                                                        slot2.getStartTime(), slot2.getEndTime()));
                                }
                        }
                }
        }

        private void validateSingleTimeSlot(TimeSlotRequest slot) {
                if (slot.getStartTime().isAfter(slot.getEndTime()) ||
                                slot.getStartTime().equals(slot.getEndTime())) {
                        throw new InvalidException("Start time must be before end time");
                }
        }

        private DoctorMultipleTimeSlotsResponse convertToResponse(Long doctorId, DayOfWeek dayOfWeek,
                        List<DoctorWorkingTimeSlot> timeSlots) {
                List<TimeSlotResponse> slotResponses = timeSlots.stream()
                                .map(this::convertSlotToResponse)
                                .collect(Collectors.toList());

                return DoctorMultipleTimeSlotsResponse.builder()
                                .doctorId(doctorId)
                                .dayOfWeek(dayOfWeek)
                                .timeSlots(slotResponses)
                                .build();
        }

        private TimeSlotResponse convertSlotToResponse(DoctorWorkingTimeSlot slot) {
                return TimeSlotResponse.builder()
                                .id(slot.getId())
                                .startTime(slot.getStartTime())
                                .endTime(slot.getEndTime())
                                .isActive(slot.getIsActive())
                                .build();
        }
}
