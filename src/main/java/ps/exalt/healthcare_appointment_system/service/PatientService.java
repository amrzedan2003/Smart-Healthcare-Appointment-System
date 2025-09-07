package ps.exalt.healthcare_appointment_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.exalt.healthcare_appointment_system.dto.request.PatientCreateRequest;
import ps.exalt.healthcare_appointment_system.dto.request.PatientUpdateRequest;
import ps.exalt.healthcare_appointment_system.dto.response.PatientResponse;
import ps.exalt.healthcare_appointment_system.entity.Patient;
import ps.exalt.healthcare_appointment_system.entity.User;
import ps.exalt.healthcare_appointment_system.enums.UserRole;
import ps.exalt.healthcare_appointment_system.exception.NotFoundException;
import ps.exalt.healthcare_appointment_system.repository.jpa.PatientRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserService userService;

    public PatientResponse registerPatient(PatientCreateRequest request) {
        // Create user entity from request
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .build();

        // Create user with PATIENT role
        User savedUser = userService.createUser(user, UserRole.PATIENT);

        // Create patient entity
        Patient patient = Patient.builder()
                .user(savedUser)
                .bloodType(request.getBloodType())
                .heightCm(request.getHeightCm())
                .weightKg(request.getWeightKg())
                .build();

        Patient savedPatient = patientRepository.save(patient);

        return convertToPatientResponse(savedPatient);
    }

    public PatientResponse updatePatientDetails(Long patientId, PatientUpdateRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));

        // Update user information
        User userToUpdate = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .build();

        userService.updateUser(patient.getUser().getId(), userToUpdate);

        // Update patient information
        if (request.getBloodType() != null) {
            patient.setBloodType(request.getBloodType());
        }
        if (request.getHeightCm() != null) {
            patient.setHeightCm(request.getHeightCm());
        }
        if (request.getWeightKg() != null) {
            patient.setWeightKg(request.getWeightKg());
        }

        Patient savedPatient = patientRepository.save(patient);

        return convertToPatientResponse(savedPatient);
    }

    public PatientResponse updatePatient(Long patientId, PatientUpdateRequest request) {
        return updatePatientDetails(patientId, request);
    }

    public void removePatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));

        // Check if patient has appointments
        if (patient.getAppointments() != null && !patient.getAppointments().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete patient with existing appointments. Please cancel all appointments first.");
        }

        patientRepository.delete(patient);
        userService.deleteUser(patient.getUser().getId());
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));

        return convertToPatientResponse(patient);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientByUserId(Long userId) {
        Patient patient = patientRepository.findByUser_Id(userId)
                .orElseThrow(() -> new NotFoundException("Patient not found with user ID: " + userId));

        return convertToPatientResponse(patient);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientByEmail(String email) {
        Patient patient = patientRepository.findByUser_Email(email)
                .orElseThrow(() -> new NotFoundException("Patient not found with email: " + email));

        return convertToPatientResponse(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(this::convertToPatientResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> searchPatientsByName(String firstName, String lastName) {
        List<Patient> patients = patientRepository
                .findByUser_FirstNameContainingIgnoreCaseOrUser_LastNameContainingIgnoreCase(firstName, lastName);

        return patients.stream()
                .map(this::convertToPatientResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return patientRepository.existsByUser_Email(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByUserId(Long userId) {
        return patientRepository.existsByUser_Id(userId);
    }

    private PatientResponse convertToPatientResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .user(userService.convertToUserResponse(patient.getUser()))
                .bloodType(patient.getBloodType())
                .heightCm(patient.getHeightCm())
                .weightKg(patient.getWeightKg())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}
