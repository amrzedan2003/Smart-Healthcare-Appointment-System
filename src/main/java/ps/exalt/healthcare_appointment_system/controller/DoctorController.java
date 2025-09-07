package ps.exalt.healthcare_appointment_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ps.exalt.healthcare_appointment_system.dto.request.DoctorCreateRequest;
import ps.exalt.healthcare_appointment_system.dto.request.DoctorUpdateRequest;
import ps.exalt.healthcare_appointment_system.dto.response.DoctorResponse;
import ps.exalt.healthcare_appointment_system.dto.response.DoctorSearchResponse;
import ps.exalt.healthcare_appointment_system.enums.DoctorStatus;
import ps.exalt.healthcare_appointment_system.service.DoctorService;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * POST /api/doctors
     */
    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorCreateRequest request) {
        DoctorResponse response = doctorService.createDoctor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * PUT /api/doctors/{doctorId}
     */
    @PutMapping("/{doctorId}")
    public ResponseEntity<DoctorResponse> updateDoctor(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorUpdateRequest request) {
        DoctorResponse response = doctorService.updateDoctor(doctorId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/doctors/{doctorId}
     */
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<Void> removeDoctor(@PathVariable Long doctorId) {
        doctorService.removeDoctor(doctorId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/doctors/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        List<DoctorResponse> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    /**
     * GET /api/doctors/{doctorId}
     */
    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long doctorId) {
        DoctorResponse response = doctorService.getDoctorById(doctorId);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/doctors/{doctorId}/status
     */
    @PatchMapping("/{doctorId}/status")
    public ResponseEntity<DoctorResponse> changeDoctorStatus(
            @PathVariable Long doctorId,
            @RequestParam DoctorStatus status) {
        DoctorResponse response = doctorService.changeDoctorStatus(doctorId, status);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/doctors/search/specialty?specialty={specialty}
     */
    @GetMapping("/search/specialty")
    public ResponseEntity<List<DoctorSearchResponse>> searchDoctorsBySpecialty(
            @RequestParam String specialty) {
        List<DoctorSearchResponse> doctors = doctorService.searchDoctorsBySpecialty(specialty);
        return ResponseEntity.ok(doctors);
    }

    /**
     * GET /api/doctors/search/name?firstName={firstName}&lastName={lastName}
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<DoctorSearchResponse>> searchDoctorsByName(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        List<DoctorSearchResponse> doctors = doctorService.searchDoctorsByName(firstName, lastName);
        return ResponseEntity.ok(doctors);
    }

    /**
     * GET /api/doctors/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<DoctorSearchResponse>> getAllActiveDoctors() {
        List<DoctorSearchResponse> doctors = doctorService.getAllActiveDoctors();
        return ResponseEntity.ok(doctors);
    }

    /**
     * GET /api/doctors/specialties
     */
    @GetMapping("/specialties")
    public ResponseEntity<List<String>> getAllSpecialties() {
        List<String> specialties = doctorService.getAllSpecialties();
        return ResponseEntity.ok(specialties);
    }
}
