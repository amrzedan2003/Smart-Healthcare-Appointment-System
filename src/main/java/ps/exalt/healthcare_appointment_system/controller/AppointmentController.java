package ps.exalt.healthcare_appointment_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ps.exalt.healthcare_appointment_system.dto.request.AppointmentBookRequest;
import ps.exalt.healthcare_appointment_system.dto.request.AppointmentCompleteRequest;
import ps.exalt.healthcare_appointment_system.dto.response.AppointmentResponse;
import ps.exalt.healthcare_appointment_system.dto.response.AvailableSlotResponse;
import ps.exalt.healthcare_appointment_system.service.AppointmentService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppointmentController {
    private final AppointmentService appointmentService;

    /**
     * POST /api/appointments/book
     */
    @PostMapping("/book")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @Valid @RequestBody AppointmentBookRequest request,
            Authentication authentication) {
        AppointmentResponse response = appointmentService.bookAppointment(request, authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * PUT /api/appointments/{appointmentId}/cancel
     */
    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication) {
        appointmentService.cancelAppointment(appointmentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * PUT /api/appointments/{appointmentId}/complete
     */
    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentCompleteRequest request,
            Authentication authentication) {
        AppointmentResponse response = appointmentService.completeAppointment(appointmentId, request,
                authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/appointments/slots/{doctorId}?date={date}
     */
    @GetMapping("/slots/{doctorId}")
    public ResponseEntity<AvailableSlotResponse> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AvailableSlotResponse response = appointmentService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/appointments/patient/my-appointments
     */
    @GetMapping("/patient/my-appointments")
    public ResponseEntity<List<AppointmentResponse>> getPatientAppointments(Authentication authentication) {
        List<AppointmentResponse> appointments = appointmentService.getPatientAppointments(authentication.getName());
        return ResponseEntity.ok(appointments);
    }

    /**
     * GET /api/appointments/doctor/my-appointments
     */
    @GetMapping("/doctor/my-appointments")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(Authentication authentication) {
        List<AppointmentResponse> appointments = appointmentService.getDoctorAppointments(authentication.getName());
        return ResponseEntity.ok(appointments);
    }

    /**
     * GET /api/appointments/{appointmentId}
     * Get appointment details for authenticated user (patient or doctor)
     */
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @PathVariable Long appointmentId,
            Authentication authentication) {
        AppointmentResponse appointment = appointmentService.getAppointmentById(appointmentId,
                authentication.getName());
        return ResponseEntity.ok(appointment);
    }
}
