package ps.exalt.healthcare_appointment_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ps.exalt.healthcare_appointment_system.dto.request.PrescriptionCreateRequest;
import ps.exalt.healthcare_appointment_system.dto.response.PrescriptionResponse;
import ps.exalt.healthcare_appointment_system.service.PrescriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionResponse> createPrescription(
            @Valid @RequestBody PrescriptionCreateRequest request,
            Authentication authentication) {

        PrescriptionResponse response = prescriptionService.createPrescription(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionResponse>> getPatientPrescriptions(
            @PathVariable Long patientId) {

        List<PrescriptionResponse> prescriptions = prescriptionService.getPatientPrescriptions(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/my-records")
    public ResponseEntity<List<PrescriptionResponse>> getMyPrescriptions(Authentication authentication) {

        List<PrescriptionResponse> prescriptions = prescriptionService.getMyPrescriptions(authentication.getName());
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/doctor/my-prescriptions")
    public ResponseEntity<List<PrescriptionResponse>> getMyDoctorPrescriptions(Authentication authentication) {

        List<PrescriptionResponse> prescriptions = prescriptionService
                .getMyDoctorPrescriptions(authentication.getName());
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/{prescriptionId}")
    public ResponseEntity<PrescriptionResponse> getPrescriptionById(@PathVariable String prescriptionId) {
        PrescriptionResponse prescription = prescriptionService.getPrescriptionById(prescriptionId);
        return ResponseEntity.ok(prescription);
    }
}
