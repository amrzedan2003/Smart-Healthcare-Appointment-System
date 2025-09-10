package ps.exalt.healthcare_appointment_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * POST /api/appointments/book/{patientId}
     */
    @PostMapping("/book/{patientId}")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @PathVariable Long patientId,
            @Valid @RequestBody AppointmentBookRequest request) {
        AppointmentResponse response = appointmentService.bookAppointment(patientId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * PUT /api/appointments/{appointmentId}/cancel/{patientId}
     */
    @PutMapping("/{appointmentId}/cancel/{patientId}")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long appointmentId,
            @PathVariable Long patientId) {
        appointmentService.cancelAppointment(patientId, appointmentId);
        return ResponseEntity.ok().build();
    }

    /**
     * PUT /api/appointments/{appointmentId}/complete/{doctorId}
     */
    @PutMapping("/{appointmentId}/complete/{doctorId}")
    public ResponseEntity<AppointmentResponse> completeAppointment(
            @PathVariable Long appointmentId,
            @PathVariable Long doctorId,
            @Valid @RequestBody AppointmentCompleteRequest request) {
        AppointmentResponse response = appointmentService.completeAppointment(doctorId, appointmentId, request);
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
     * GET /api/appointments/patient/{patientId}
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> getPatientAppointments(@PathVariable Long patientId) {
        List<AppointmentResponse> appointments = appointmentService.getPatientAppointments(patientId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * GET /api/appointments/doctor/{doctorId}
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(@PathVariable Long doctorId) {
        List<AppointmentResponse> appointments = appointmentService.getDoctorAppointments(doctorId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * GET /api/appointments/{appointmentId}
     */
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long appointmentId) {
        AppointmentResponse appointment = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(appointment);
    }
}
