package ps.exalt.healthcare_appointment_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.exalt.healthcare_appointment_system.dto.request.DoctorCreateRequest;
import ps.exalt.healthcare_appointment_system.dto.request.DoctorUpdateRequest;
import ps.exalt.healthcare_appointment_system.dto.response.DoctorResponse;
import ps.exalt.healthcare_appointment_system.dto.response.DoctorSearchResponse;
import ps.exalt.healthcare_appointment_system.entity.Doctor;
import ps.exalt.healthcare_appointment_system.entity.User;
import ps.exalt.healthcare_appointment_system.enums.DoctorStatus;
import ps.exalt.healthcare_appointment_system.enums.UserRole;
import ps.exalt.healthcare_appointment_system.exception.NotFoundException;
import ps.exalt.healthcare_appointment_system.repository.jpa.DoctorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserService userService;

    public DoctorResponse createDoctor(DoctorCreateRequest request) {
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

        // Create user with DOCTOR role
        User savedUser = userService.createUser(user, UserRole.DOCTOR);

        // Create doctor entity
        Doctor doctor = Doctor.builder()
                .user(savedUser)
                .specialization(request.getSpecialization())
                .status(DoctorStatus.ACTIVE)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        return convertToDoctorResponse(savedDoctor);
    }

    public DoctorResponse updateDoctor(Long doctorId, DoctorUpdateRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + doctorId));

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

        userService.updateUser(doctor.getUser().getId(), userToUpdate);

        // Update doctor information
        if (request.getSpecialization() != null) {
            doctor.setSpecialization(request.getSpecialization());
        }
        if (request.getStatus() != null) {
            doctor.setStatus(request.getStatus());
        }

        Doctor savedDoctor = doctorRepository.save(doctor);

        return convertToDoctorResponse(savedDoctor);
    }

    public void removeDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + doctorId));

        // Check if doctor has active appointments
        if (doctor.getAppointments() != null && !doctor.getAppointments().isEmpty()) {
            // Set status to INACTIVE
            doctor.setStatus(DoctorStatus.INACTIVE);
            doctorRepository.save(doctor);
        } else {
            // Hard delete if no appointments
            doctorRepository.delete(doctor);
            userService.deleteUser(doctor.getUser().getId());
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "doctors", key = "'specialty_' + #specialty")
    public List<DoctorSearchResponse> searchDoctorsBySpecialty(String specialty) {
        List<Doctor> doctors = doctorRepository.findBySpecialization(specialty);

        return doctors.stream()
                .filter(doctor -> doctor.getStatus() == DoctorStatus.ACTIVE)
                .map(this::convertToDoctorSearchResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "doctors", key = "'active_doctors'")
    public List<DoctorSearchResponse> getAllActiveDoctors() {
        List<Doctor> doctors = doctorRepository.findByStatus(DoctorStatus.ACTIVE);

        return doctors.stream()
                .map(this::convertToDoctorSearchResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();

        return doctors.stream()
                .map(this::convertToDoctorResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "doctors", key = "#doctorId")
    public DoctorResponse getDoctorById(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + doctorId));

        return convertToDoctorResponse(doctor);
    }

    @Transactional(readOnly = true)
    public List<DoctorSearchResponse> searchDoctorsByName(String firstName, String lastName) {
        List<Doctor> doctors = doctorRepository
                .findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCase(firstName, lastName);

        return doctors.stream()
                .filter(doctor -> doctor.getStatus() == DoctorStatus.ACTIVE)
                .map(this::convertToDoctorSearchResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "doctors", key = "'specialties'")
    public List<String> getAllSpecialties() {
        return doctorRepository.findAll().stream()
                .filter(doctor -> doctor.getStatus() == DoctorStatus.ACTIVE)
                .map(Doctor::getSpecialization)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public DoctorResponse changeDoctorStatus(Long doctorId, DoctorStatus status) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + doctorId));

        doctor.setStatus(status);
        Doctor savedDoctor = doctorRepository.save(doctor);

        return convertToDoctorResponse(savedDoctor);
    }

    private DoctorResponse convertToDoctorResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .user(userService.convertToUserResponse(doctor.getUser()))
                .specialization(doctor.getSpecialization())
                .status(doctor.getStatus())
                .createdAt(doctor.getCreatedAt())
                .updatedAt(doctor.getUpdatedAt())
                .build();
    }

    private DoctorSearchResponse convertToDoctorSearchResponse(Doctor doctor) {
        return DoctorSearchResponse.builder()
                .id(doctor.getId())
                .firstName(doctor.getUser().getFirstName())
                .lastName(doctor.getUser().getLastName())
                .specialization(doctor.getSpecialization())
                .status(doctor.getStatus())
                .phoneNumber(doctor.getUser().getPhoneNumber())
                .build();
    }
}
