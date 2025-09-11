package ps.exalt.healthcare_appointment_system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ps.exalt.healthcare_appointment_system.dto.request.DoctorCreateRequest;
import ps.exalt.healthcare_appointment_system.dto.request.DoctorUpdateRequest;
import ps.exalt.healthcare_appointment_system.dto.response.DoctorResponse;
import ps.exalt.healthcare_appointment_system.dto.response.DoctorSearchResponse;
import ps.exalt.healthcare_appointment_system.dto.response.UserResponse;
import ps.exalt.healthcare_appointment_system.entity.Doctor;
import ps.exalt.healthcare_appointment_system.entity.User;
import ps.exalt.healthcare_appointment_system.enums.DoctorStatus;
import ps.exalt.healthcare_appointment_system.enums.Gender;
import ps.exalt.healthcare_appointment_system.enums.UserRole;
import ps.exalt.healthcare_appointment_system.exception.NotFoundException;
import ps.exalt.healthcare_appointment_system.repository.jpa.DoctorRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private DoctorService doctorService;

    private User mockUser;
    private Doctor mockDoctor;
    private UserResponse mockUserResponse;
    private DoctorCreateRequest createRequest;
    private DoctorUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .firstName("Dr. John")
                .lastName("Smith")
                .email("doctor@test.com")
                .phoneNumber("1234567890")
                .dateOfBirth(LocalDate.of(1980, 5, 15))
                .gender(Gender.MALE)
                .address("123 Main St")
                .build();

        mockUserResponse = UserResponse.builder()
                .id(1L)
                .firstName("Dr. John")
                .lastName("Smith")
                .email("doctor@test.com")
                .phoneNumber("1234567890")
                .dateOfBirth(LocalDate.of(1980, 5, 15))
                .gender(Gender.MALE)
                .address("123 Main St")
                .build();

        mockDoctor = Doctor.builder()
                .id(1L)
                .user(mockUser)
                .specialization("Cardiology")
                .status(DoctorStatus.ACTIVE)
                .build();

        createRequest = DoctorCreateRequest.builder()
                .firstName("Dr. John")
                .lastName("Smith")
                .email("doctor@test.com")
                .password("password123")
                .phoneNumber("1234567890")
                .dateOfBirth(LocalDate.of(1980, 5, 15))
                .gender(Gender.MALE)
                .address("123 Main St")
                .specialization("Cardiology")
                .build();

        updateRequest = DoctorUpdateRequest.builder()
                .firstName("Dr. Jane")
                .lastName("Doe")
                .specialization("Neurology")
                .status(DoctorStatus.INACTIVE)
                .build();
    }

    @Test
    void createDoctor_Success() {
        // Given
        when(userService.createUser(any(User.class), eq(UserRole.DOCTOR))).thenReturn(mockUser);
        when(userService.convertToUserResponse(mockUser)).thenReturn(mockUserResponse);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(mockDoctor);

        // When
        DoctorResponse response = doctorService.createDoctor(createRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Dr. John", response.getUser().getFirstName());
        assertEquals("Smith", response.getUser().getLastName());
        assertEquals("doctor@test.com", response.getUser().getEmail());
        assertEquals("Cardiology", response.getSpecialization());
        assertEquals(DoctorStatus.ACTIVE, response.getStatus());

        verify(userService).createUser(any(User.class), eq(UserRole.DOCTOR));
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void updateDoctor_Success() {
        // Given
        Long doctorId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(mockDoctor));
        when(userService.convertToUserResponse(mockUser)).thenReturn(mockUserResponse);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(mockDoctor);

        // When
        DoctorResponse response = doctorService.updateDoctor(doctorId, updateRequest);

        // Then
        assertNotNull(response);
        verify(userService).updateUser(eq(mockUser.getId()), any(User.class));
        verify(doctorRepository).save(mockDoctor);

        // Verify that doctor fields were updated
        assertEquals("Neurology", mockDoctor.getSpecialization());
        assertEquals(DoctorStatus.INACTIVE, mockDoctor.getStatus());
    }

    @Test
    void updateDoctor_DoctorNotFound_ThrowsNotFoundException() {
        // Given
        Long doctorId = 999L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> doctorService.updateDoctor(doctorId, updateRequest));

        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void removeDoctor_Success() {
        // Given
        Long doctorId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(mockDoctor));

        // When
        doctorService.removeDoctor(doctorId);

        // Then
        verify(doctorRepository).delete(mockDoctor);
        verify(userService).deleteUser(mockUser.getId());
    }

    @Test
    void removeDoctor_DoctorNotFound_ThrowsNotFoundException() {
        // Given
        Long doctorId = 999L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> doctorService.removeDoctor(doctorId));

        verify(doctorRepository, never()).delete(any(Doctor.class));
    }

    @Test
    void getDoctorById_Success() {
        // Given
        Long doctorId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(mockDoctor));
        when(userService.convertToUserResponse(mockUser)).thenReturn(mockUserResponse);

        // When
        DoctorResponse response = doctorService.getDoctorById(doctorId);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Dr. John", response.getUser().getFirstName());
        assertEquals("Smith", response.getUser().getLastName());
        assertEquals("Cardiology", response.getSpecialization());
    }

    @Test
    void getDoctorById_DoctorNotFound_ThrowsNotFoundException() {
        // Given
        Long doctorId = 999L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> doctorService.getDoctorById(doctorId));
    }

    @Test
    void getAllDoctors_Success() {
        // Given
        User mockUser2 = User.builder()
                .id(2L)
                .firstName("Dr. Jane")
                .lastName("Doe")
                .email("doctor2@test.com")
                .build();

        Doctor mockDoctor2 = Doctor.builder()
                .id(2L)
                .user(mockUser2)
                .specialization("Neurology")
                .status(DoctorStatus.ACTIVE)
                .build();

        List<Doctor> doctors = Arrays.asList(mockDoctor, mockDoctor2);
        when(doctorRepository.findAll()).thenReturn(doctors);

        // When
        List<DoctorResponse> responses = doctorService.getAllDoctors();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        DoctorResponse first = responses.get(0);
        assertEquals(1L, first.getId());
        assertEquals("Cardiology", first.getSpecialization());

        DoctorResponse second = responses.get(1);
        assertEquals(2L, second.getId());
        assertEquals("Neurology", second.getSpecialization());
    }

    @Test
    void searchDoctorsBySpecialty_Success() {
        // Given
        String specialty = "Cardiology";
        List<Doctor> doctors = Arrays.asList(mockDoctor);
        when(doctorRepository.findBySpecialization(specialty)).thenReturn(doctors);

        // When
        List<DoctorSearchResponse> responses = doctorService.searchDoctorsBySpecialty(specialty);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Cardiology", responses.get(0).getSpecialization());
    }

    @Test
    void getDoctorByEmail_Success() {
        // Given
        String email = "doctor@test.com";
        when(doctorRepository.findByUserEmail(email)).thenReturn(Optional.of(mockDoctor));

        // When
        Optional<Doctor> foundDoctor = doctorRepository.findByUserEmail(email);

        // Then
        assertTrue(foundDoctor.isPresent());
        assertEquals(email, foundDoctor.get().getUser().getEmail());
    }

    @Test
    void getDoctorByEmail_NotFound() {
        // Given
        String email = "nonexistent@test.com";
        when(doctorRepository.findByUserEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<Doctor> foundDoctor = doctorRepository.findByUserEmail(email);

        // Then
        assertFalse(foundDoctor.isPresent());
    }
}
