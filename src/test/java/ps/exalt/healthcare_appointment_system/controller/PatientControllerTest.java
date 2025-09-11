package ps.exalt.healthcare_appointment_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ps.exalt.healthcare_appointment_system.dto.request.PatientCreateRequest;
import ps.exalt.healthcare_appointment_system.dto.request.PatientUpdateRequest;
import ps.exalt.healthcare_appointment_system.dto.response.PatientResponse;
import ps.exalt.healthcare_appointment_system.dto.response.UserResponse;
import ps.exalt.healthcare_appointment_system.enums.BloodType;
import ps.exalt.healthcare_appointment_system.enums.Gender;
import ps.exalt.healthcare_appointment_system.service.PatientService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PatientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private ObjectMapper objectMapper;
    private PatientCreateRequest patientCreateRequest;
    private PatientUpdateRequest patientUpdateRequest;
    private PatientResponse patientResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDate serialization

        patientCreateRequest = PatientCreateRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .password("password123")
                .phoneNumber("+970123456789")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .address("123 Main St")
                .bloodType(BloodType.A_POSITIVE)
                .heightCm(180)
                .weightKg(75.0)
                .build();

        patientUpdateRequest = PatientUpdateRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .password("password123")
                .phoneNumber("+970123456789")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .address("123 Main St")
                .bloodType(BloodType.A_POSITIVE)
                .heightCm(180)
                .weightKg(75.0)
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .phoneNumber("+970123456789")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .address("123 Main St")
                .roleName("PATIENT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        patientResponse = PatientResponse.builder()
                .id(1L)
                .user(userResponse)
                .bloodType(BloodType.A_POSITIVE)
                .heightCm(180)
                .weightKg(75.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void registerPatient_Success() throws Exception {
        when(patientService.registerPatient(any(PatientCreateRequest.class)))
                .thenReturn(patientResponse);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.firstName").value("John"))
                .andExpect(jsonPath("$.user.lastName").value("Doe"))
                .andExpect(jsonPath("$.user.email").value("john.doe@email.com"))
                .andExpect(jsonPath("$.user.gender").value("MALE"))
                .andExpect(jsonPath("$.bloodType").value("A_POSITIVE"));
    }

    @Test
    void updatePatient_Success() throws Exception {
        when(patientService.updatePatient(eq(1L), any(PatientUpdateRequest.class)))
                .thenReturn(patientResponse);

        mockMvc.perform(put("/api/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.firstName").value("John"))
                .andExpect(jsonPath("$.user.lastName").value("Doe"))
                .andExpect(jsonPath("$.user.email").value("john.doe@email.com"))
                .andExpect(jsonPath("$.user.gender").value("MALE"))
                .andExpect(jsonPath("$.bloodType").value("A_POSITIVE"));
    }

    @Test
    void getPatientById_Success() throws Exception {
        when(patientService.getPatientById(1L))
                .thenReturn(patientResponse);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.firstName").value("John"))
                .andExpect(jsonPath("$.user.lastName").value("Doe"))
                .andExpect(jsonPath("$.user.email").value("john.doe@email.com"))
                .andExpect(jsonPath("$.user.gender").value("MALE"))
                .andExpect(jsonPath("$.bloodType").value("A_POSITIVE"));
    }

    @Test
    void getAllPatients_Success() throws Exception {
        List<PatientResponse> patients = Arrays.asList(patientResponse);
        when(patientService.getAllPatients())
                .thenReturn(patients);

        mockMvc.perform(get("/api/patients/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].user.firstName").value("John"))
                .andExpect(jsonPath("$[0].user.lastName").value("Doe"))
                .andExpect(jsonPath("$[0].user.gender").value("MALE"))
                .andExpect(jsonPath("$[0].bloodType").value("A_POSITIVE"));
    }

    @Test
    void removePatient_Success() throws Exception {
        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchPatientsByName_Success() throws Exception {
        List<PatientResponse> patients = Arrays.asList(patientResponse);
        when(patientService.searchPatientsByName("John", "Doe"))
                .thenReturn(patients);

        mockMvc.perform(get("/api/patients/search?firstName=John&lastName=Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].user.firstName").value("John"))
                .andExpect(jsonPath("$[0].user.lastName").value("Doe"))
                .andExpect(jsonPath("$[0].user.gender").value("MALE"))
                .andExpect(jsonPath("$[0].bloodType").value("A_POSITIVE"));
    }

    @Test
    void getPatientByEmail_Success() throws Exception {
        when(patientService.getPatientByEmail("john.doe@email.com"))
                .thenReturn(patientResponse);

        mockMvc.perform(get("/api/patients/email?email=john.doe@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.firstName").value("John"))
                .andExpect(jsonPath("$.user.lastName").value("Doe"))
                .andExpect(jsonPath("$.user.email").value("john.doe@email.com"))
                .andExpect(jsonPath("$.user.gender").value("MALE"))
                .andExpect(jsonPath("$.bloodType").value("A_POSITIVE"));
    }
}
