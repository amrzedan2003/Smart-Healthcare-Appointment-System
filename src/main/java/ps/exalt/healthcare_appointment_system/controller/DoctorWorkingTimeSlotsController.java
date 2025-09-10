package ps.exalt.healthcare_appointment_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ps.exalt.healthcare_appointment_system.dto.request.DoctorMultipleTimeSlotsRequest;
import ps.exalt.healthcare_appointment_system.dto.request.TimeSlotRequest;
import ps.exalt.healthcare_appointment_system.dto.response.DoctorMultipleTimeSlotsResponse;
import ps.exalt.healthcare_appointment_system.dto.response.TimeSlotResponse;
import ps.exalt.healthcare_appointment_system.service.DoctorWorkingTimeSlotsService;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/doctors/{doctorId}/time-slots")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DoctorWorkingTimeSlotsController {

    private final DoctorWorkingTimeSlotsService timeSlotsService;

    /**
     * POST /api/doctors/{doctorId}/time-slots
     * Set multiple working time slots for a doctor on a specific day
     */
    @PostMapping
    public ResponseEntity<DoctorMultipleTimeSlotsResponse> setDoctorTimeSlots(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorMultipleTimeSlotsRequest request) {
        log.info("Setting multiple time slots for doctor ID: {} on {}", doctorId, request.getDayOfWeek());
        DoctorMultipleTimeSlotsResponse response = timeSlotsService.setDoctorTimeSlots(doctorId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * POST /api/doctors/{doctorId}/time-slots/{dayOfWeek}
     * Add a single time slot for a doctor on a specific day
     */
    @PostMapping("/{dayOfWeek}")
    public ResponseEntity<DoctorMultipleTimeSlotsResponse> addTimeSlot(
            @PathVariable Long doctorId,
            @PathVariable DayOfWeek dayOfWeek,
            @Valid @RequestBody TimeSlotRequest request) {
        log.info("Adding time slot for doctor ID: {} on {} from {} to {}",
                doctorId, dayOfWeek, request.getStartTime(), request.getEndTime());
        DoctorMultipleTimeSlotsResponse response = timeSlotsService.addTimeSlot(doctorId, dayOfWeek, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/doctors/{doctorId}/time-slots/{dayOfWeek}
     * Get all time slots for a doctor on a specific day
     */
    @GetMapping("/{dayOfWeek}")
    public ResponseEntity<DoctorMultipleTimeSlotsResponse> getDoctorTimeSlots(
            @PathVariable Long doctorId,
            @PathVariable DayOfWeek dayOfWeek) {
        log.info("Getting time slots for doctor ID: {} on {}", doctorId, dayOfWeek);
        DoctorMultipleTimeSlotsResponse response = timeSlotsService.getDoctorTimeSlots(doctorId, dayOfWeek);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/doctors/{doctorId}/time-slots/{dayOfWeek}/active
     * Get only active time slots for a doctor on a specific day
     */
    @GetMapping("/{dayOfWeek}/active")
    public ResponseEntity<DoctorMultipleTimeSlotsResponse> getActiveDoctorTimeSlots(
            @PathVariable Long doctorId,
            @PathVariable DayOfWeek dayOfWeek) {
        log.info("Getting active time slots for doctor ID: {} on {}", doctorId, dayOfWeek);
        DoctorMultipleTimeSlotsResponse response = timeSlotsService.getActiveDoctorTimeSlots(doctorId, dayOfWeek);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/doctors/{doctorId}/time-slots
     * Get all time slots for a doctor (all days)
     */
    @GetMapping
    public ResponseEntity<List<DoctorMultipleTimeSlotsResponse>> getAllDoctorTimeSlots(@PathVariable Long doctorId) {
        log.info("Getting all time slots for doctor ID: {}", doctorId);
        List<DoctorMultipleTimeSlotsResponse> response = timeSlotsService.getAllDoctorTimeSlots(doctorId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/doctors/{doctorId}/time-slots/active
     * Get all active time slots for a doctor (all days)
     */
    @GetMapping("/active")
    public ResponseEntity<List<DoctorMultipleTimeSlotsResponse>> getAllActiveDoctorTimeSlots(
            @PathVariable Long doctorId) {
        log.info("Getting all active time slots for doctor ID: {}", doctorId);
        List<DoctorMultipleTimeSlotsResponse> response = timeSlotsService.getAllActiveDoctorTimeSlots(doctorId);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/doctors/{doctorId}/time-slots/slot/{slotId}
     * Update a specific time slot
     */
    @PutMapping("/slot/{slotId}")
    public ResponseEntity<TimeSlotResponse> updateTimeSlot(
            @PathVariable Long doctorId,
            @PathVariable Long slotId,
            @Valid @RequestBody TimeSlotRequest request) {
        log.info("Updating time slot ID: {} for doctor ID: {}", slotId, doctorId);
        TimeSlotResponse response = timeSlotsService.updateTimeSlot(slotId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/doctors/{doctorId}/time-slots/slot/{slotId}
     * Delete a specific time slot
     */
    @DeleteMapping("/slot/{slotId}")
    public ResponseEntity<Void> deleteTimeSlot(
            @PathVariable Long doctorId,
            @PathVariable Long slotId) {
        log.info("Deleting time slot ID: {} for doctor ID: {}", slotId, doctorId);
        timeSlotsService.deleteTimeSlot(slotId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/doctors/{doctorId}/time-slots/{dayOfWeek}
     * Delete all time slots for a doctor on a specific day
     */
    @DeleteMapping("/{dayOfWeek}")
    public ResponseEntity<Void> deleteDoctorTimeSlots(
            @PathVariable Long doctorId,
            @PathVariable DayOfWeek dayOfWeek) {
        log.info("Deleting all time slots for doctor ID: {} on {}", doctorId, dayOfWeek);
        timeSlotsService.deleteDoctorTimeSlots(doctorId, dayOfWeek);
        return ResponseEntity.noContent().build();
    }
}
