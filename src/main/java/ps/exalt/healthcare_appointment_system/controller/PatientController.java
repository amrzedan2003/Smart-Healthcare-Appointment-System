package ps.exalt.healthcare_appointment_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ps.exalt.healthcare_appointment_system.dto.request.PatientCreateRequest;
import ps.exalt.healthcare_appointment_system.dto.request.PatientUpdateRequest;
import ps.exalt.healthcare_appointment_system.dto.response.PatientResponse;
import ps.exalt.healthcare_appointment_system.service.PatientService;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    /**
     * POST /api/patients
     */
    @PostMapping
    public ResponseEntity<PatientResponse> registerPatient(@Valid @RequestBody PatientCreateRequest request) {
        PatientResponse response = patientService.registerPatient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * PUT /api/patients/{patientId}
     */
    @PutMapping("/{patientId}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long patientId,
            @Valid @RequestBody PatientUpdateRequest request) {
        PatientResponse response = patientService.updatePatient(patientId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/patients/{patientId}
     */
    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> removePatient(@PathVariable Long patientId) {
        patientService.removePatient(patientId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/patients/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<PatientResponse> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    /**
     * GET /api/patients/search?firstName={firstName}&lastName={lastName}
     */
    @GetMapping("/search")
    public ResponseEntity<List<PatientResponse>> searchPatientsByName(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        List<PatientResponse> patients = patientService.searchPatientsByName(firstName, lastName);
        return ResponseEntity.ok(patients);
    }

    /**
     * GET /api/patients/{patientId}
     */
    @GetMapping("/{patientId}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long patientId) {
        PatientResponse response = patientService.getPatientById(patientId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/patients/email?email={email}
     */
    @GetMapping("/email")
    public ResponseEntity<PatientResponse> getPatientByEmail(@RequestParam String email) {
        PatientResponse response = patientService.getPatientByEmail(email);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/patients/exists/email?email={email}
     */
    @GetMapping("/exists/email")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        boolean exists = patientService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    /**
     * GET /api/patients/exists/user/{userId}
     */
    @GetMapping("/exists/user/{userId}")
    public ResponseEntity<Boolean> existsByUserId(@PathVariable Long userId) {
        boolean exists = patientService.existsByUserId(userId);
        return ResponseEntity.ok(exists);
    }
}
