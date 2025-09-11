package ps.exalt.healthcare_appointment_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ps.exalt.healthcare_appointment_system.dto.request.DoctorMultipleTimeSlotsRequest;
import ps.exalt.healthcare_appointment_system.dto.request.TimeSlotRequest;
import ps.exalt.healthcare_appointment_system.dto.response.DoctorMultipleTimeSlotsResponse;
import ps.exalt.healthcare_appointment_system.dto.response.TimeSlotResponse;
import ps.exalt.healthcare_appointment_system.service.DoctorWorkingTimeSlotsService;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/doctors/time-slots")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorWorkingTimeSlotsController {

    private final DoctorWorkingTimeSlotsService timeSlotsService;

    /**
     * POST /api/doctors/time-slots
     * Set multiple working time slots for authenticated doctor on a specific day
     */
    @PostMapping
    public ResponseEntity<DoctorMultipleTimeSlotsResponse> setDoctorTimeSlots(
            @Valid @RequestBody DoctorMultipleTimeSlotsRequest request,
            Authentication authentication) {
        DoctorMultipleTimeSlotsResponse response = timeSlotsService.setDoctorTimeSlots(request,
                authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * POST /api/doctors/time-slots/{dayOfWeek}
     * Add a single time slot for authenticated doctor on a specific day
     */
    @PostMapping("/{dayOfWeek}")
    public ResponseEntity<DoctorMultipleTimeSlotsResponse> addTimeSlot(
            @PathVariable DayOfWeek dayOfWeek,
            @Valid @RequestBody TimeSlotRequest request,
            Authentication authentication) {
        DoctorMultipleTimeSlotsResponse response = timeSlotsService.addTimeSlot(dayOfWeek, request,
                authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/doctors/time-slots/{dayOfWeek}
     * Get all time slots for authenticated doctor on a specific day
     */
    @GetMapping("/{dayOfWeek}")
    public ResponseEntity<DoctorMultipleTimeSlotsResponse> getDoctorTimeSlots(
            @PathVariable DayOfWeek dayOfWeek,
            Authentication authentication) {
        DoctorMultipleTimeSlotsResponse response = timeSlotsService.getDoctorTimeSlots(dayOfWeek,
                authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/doctors/time-slots/{dayOfWeek}/active
     * Get only active time slots for authenticated doctor on a specific day
     */
    @GetMapping("/{dayOfWeek}/active")
    public ResponseEntity<DoctorMultipleTimeSlotsResponse> getActiveDoctorTimeSlots(
            @PathVariable DayOfWeek dayOfWeek,
            Authentication authentication) {
        DoctorMultipleTimeSlotsResponse response = timeSlotsService.getActiveDoctorTimeSlots(dayOfWeek,
                authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/doctors/time-slots
     * Get all time slots for authenticated doctor (all days)
     */
    @GetMapping
    public ResponseEntity<List<DoctorMultipleTimeSlotsResponse>> getAllDoctorTimeSlots(Authentication authentication) {
        List<DoctorMultipleTimeSlotsResponse> response = timeSlotsService
                .getAllDoctorTimeSlots(authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/doctors/time-slots/active
     * Get all active time slots for authenticated doctor (all days)
     */
    @GetMapping("/active")
    public ResponseEntity<List<DoctorMultipleTimeSlotsResponse>> getAllActiveDoctorTimeSlots(
            Authentication authentication) {
        List<DoctorMultipleTimeSlotsResponse> response = timeSlotsService
                .getAllActiveDoctorTimeSlots(authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/doctors/time-slots/slot/{slotId}
     * Update a specific time slot
     */
    @PutMapping("/slot/{slotId}")
    public ResponseEntity<TimeSlotResponse> updateTimeSlot(
            @PathVariable Long slotId,
            @Valid @RequestBody TimeSlotRequest request,
            Authentication authentication) {
        TimeSlotResponse response = timeSlotsService.updateTimeSlot(slotId, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/doctors/time-slots/slot/{slotId}
     * Delete a specific time slot
     */
    @DeleteMapping("/slot/{slotId}")
    public ResponseEntity<Void> deleteTimeSlot(
            @PathVariable Long slotId,
            Authentication authentication) {
        timeSlotsService.deleteTimeSlot(slotId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/doctors/time-slots/{dayOfWeek}
     * Delete all time slots for authenticated doctor on a specific day
     */
    @DeleteMapping("/{dayOfWeek}")
    public ResponseEntity<Void> deleteDoctorTimeSlots(
            @PathVariable DayOfWeek dayOfWeek,
            Authentication authentication) {
        timeSlotsService.deleteDoctorTimeSlots(dayOfWeek, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
