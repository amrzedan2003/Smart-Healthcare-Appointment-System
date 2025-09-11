package ps.exalt.healthcare_appointment_system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ps.exalt.healthcare_appointment_system.dto.request.PatientCreateRequest;
import ps.exalt.healthcare_appointment_system.dto.request.PatientUpdateRequest;
import ps.exalt.healthcare_appointment_system.dto.response.PatientResponse;
import ps.exalt.healthcare_appointment_system.dto.response.UserResponse;
import ps.exalt.healthcare_appointment_system.entity.Appointment;
import ps.exalt.healthcare_appointment_system.entity.Patient;
import ps.exalt.healthcare_appointment_system.entity.User;
import ps.exalt.healthcare_appointment_system.enums.BloodType;
import ps.exalt.healthcare_appointment_system.enums.Gender;
import ps.exalt.healthcare_appointment_system.enums.UserRole;
import ps.exalt.healthcare_appointment_system.exception.NotFoundException;
import ps.exalt.healthcare_appointment_system.repository.jpa.PatientRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PatientService patientService;

    private User mockUser;
    private Patient mockPatient;
    private UserResponse mockUserResponse;
    private PatientCreateRequest createRequest;
    private PatientUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("patient@test.com")
                .phoneNumber("1234567890")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .gender(Gender.MALE)
                .address("123 Main St")
                .build();

        mockUserResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("patient@test.com")
                .phoneNumber("1234567890")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .gender(Gender.MALE)
                .address("123 Main St")
                .build();

        mockPatient = Patient.builder()
                .id(1L)
                .user(mockUser)
                .bloodType(BloodType.O_POSITIVE)
                .heightCm(175)
                .weightKg(70.0)
                .build();

        createRequest = PatientCreateRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("patient@test.com")
                .password("password123")
                .phoneNumber("1234567890")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .gender(Gender.MALE)
                .address("123 Main St")
                .bloodType(BloodType.O_POSITIVE)
                .heightCm(175)
                .weightKg(70.0)
                .build();

        updateRequest = PatientUpdateRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .bloodType(BloodType.A_POSITIVE)
                .heightCm(165)
                .weightKg(60.0)
                .build();
    }

    @Test
    void registerPatient_Success() {
        // Given
        when(userService.createUser(any(User.class), eq(UserRole.PATIENT))).thenReturn(mockUser);
        when(userService.convertToUserResponse(mockUser)).thenReturn(mockUserResponse);
        when(patientRepository.save(any(Patient.class))).thenReturn(mockPatient);

        // When
        PatientResponse response = patientService.registerPatient(createRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getUser().getFirstName());
        assertEquals("Doe", response.getUser().getLastName());
        assertEquals("patient@test.com", response.getUser().getEmail());
        assertEquals(BloodType.O_POSITIVE, response.getBloodType());
        assertEquals(175, response.getHeightCm());
        assertEquals(70.0, response.getWeightKg());

        verify(userService).createUser(any(User.class), eq(UserRole.PATIENT));
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void updatePatient_Success() {
        // Given
        Long patientId = 1L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(mockPatient));
        when(userService.convertToUserResponse(mockUser)).thenReturn(mockUserResponse);
        when(patientRepository.save(any(Patient.class))).thenReturn(mockPatient);

        // When
        PatientResponse response = patientService.updatePatient(patientId, updateRequest);

        // Then
        assertNotNull(response);
        verify(userService).updateUser(eq(mockUser.getId()), any(User.class));
        verify(patientRepository).save(mockPatient);

        // Verify that patient fields were updated
        assertEquals(BloodType.A_POSITIVE, mockPatient.getBloodType());
        assertEquals(165, mockPatient.getHeightCm());
        assertEquals(60.0, mockPatient.getWeightKg());
    }

    @Test
    void updatePatient_PatientNotFound_ThrowsNotFoundException() {
        // Given
        Long patientId = 999L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> patientService.updatePatient(patientId, updateRequest));

        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void removePatient_Success() {
        // Given
        Long patientId = 1L;
        mockPatient.setAppointments(null); // No appointments
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(mockPatient));

        // When
        patientService.removePatient(patientId);

        // Then
        verify(patientRepository).delete(mockPatient);
        verify(userService).deleteUser(mockUser.getId());
    }

    @Test
    void removePatient_WithAppointments_ThrowsIllegalStateException() {
        // Given
        Long patientId = 1L;
        Set<Appointment> appointments = new HashSet<>();
        appointments.add(new Appointment()); // Has appointments
        mockPatient.setAppointments(appointments);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(mockPatient));

        // When & Then
        assertThrows(IllegalStateException.class, () -> patientService.removePatient(patientId));

        verify(patientRepository, never()).delete(any(Patient.class));
    }

    @Test
    void removePatient_PatientNotFound_ThrowsNotFoundException() {
        // Given
        Long patientId = 999L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> patientService.removePatient(patientId));

        verify(patientRepository, never()).delete(any(Patient.class));
    }

    @Test
    void getPatientById_Success() {
        // Given
        Long patientId = 1L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(mockPatient));
        when(userService.convertToUserResponse(mockUser)).thenReturn(mockUserResponse);

        // When
        PatientResponse response = patientService.getPatientById(patientId);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getUser().getFirstName());
        assertEquals("Doe", response.getUser().getLastName());
        assertEquals(BloodType.O_POSITIVE, response.getBloodType());
    }

    @Test
    void getPatientById_PatientNotFound_ThrowsNotFoundException() {
        // Given
        Long patientId = 999L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> patientService.getPatientById(patientId));
    }

    @Test
    void getAllPatients_Success() {
        // Given
        User mockUser2 = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("patient2@test.com")
                .build();

        UserResponse mockUserResponse2 = UserResponse.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("patient2@test.com")
                .build();

        Patient mockPatient2 = Patient.builder()
                .id(2L)
                .user(mockUser2)
                .bloodType(BloodType.A_POSITIVE)
                .heightCm(165)
                .weightKg(60.0)
                .build();

        List<Patient> patients = Arrays.asList(mockPatient, mockPatient2);
        when(patientRepository.findAll()).thenReturn(patients);
        when(userService.convertToUserResponse(mockUser)).thenReturn(mockUserResponse);
        when(userService.convertToUserResponse(mockUser2)).thenReturn(mockUserResponse2);

        // When
        List<PatientResponse> responses = patientService.getAllPatients();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        PatientResponse first = responses.get(0);
        assertEquals(1L, first.getId());
        assertEquals(BloodType.O_POSITIVE, first.getBloodType());

        PatientResponse second = responses.get(1);
        assertEquals(2L, second.getId());
        assertEquals(BloodType.A_POSITIVE, second.getBloodType());
    }

    @Test
    void searchPatientsByName_Success() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        List<Patient> patients = Arrays.asList(mockPatient);
        when(patientRepository.findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCase(firstName,
                lastName))
                .thenReturn(patients);
        when(userService.convertToUserResponse(mockUser)).thenReturn(mockUserResponse);

        // When
        List<PatientResponse> responses = patientService.searchPatientsByName(firstName, lastName);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("John", responses.get(0).getUser().getFirstName());
        assertEquals("Doe", responses.get(0).getUser().getLastName());
    }

    @Test
    void getPatientByEmail_Success() {
        // Given
        String email = "patient@test.com";
        when(patientRepository.findByUserEmail(email)).thenReturn(Optional.of(mockPatient));
        when(userService.convertToUserResponse(mockUser)).thenReturn(mockUserResponse);

        // When
        PatientResponse response = patientService.getPatientByEmail(email);

        // Then
        assertNotNull(response);
        assertEquals(email, response.getUser().getEmail());
    }

    @Test
    void getPatientByEmail_NotFound_ThrowsNotFoundException() {
        // Given
        String email = "nonexistent@test.com";
        when(patientRepository.findByUserEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> patientService.getPatientByEmail(email));
    }
}
