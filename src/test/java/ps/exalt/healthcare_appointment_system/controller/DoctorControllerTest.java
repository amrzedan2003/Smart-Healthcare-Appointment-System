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
import ps.exalt.healthcare_appointment_system.dto.request.DoctorCreateRequest;
import ps.exalt.healthcare_appointment_system.dto.request.DoctorUpdateRequest;
import ps.exalt.healthcare_appointment_system.dto.response.DoctorResponse;
import ps.exalt.healthcare_appointment_system.dto.response.DoctorSearchResponse;
import ps.exalt.healthcare_appointment_system.dto.response.UserResponse;
import ps.exalt.healthcare_appointment_system.enums.DoctorStatus;
import ps.exalt.healthcare_appointment_system.enums.Gender;
import ps.exalt.healthcare_appointment_system.service.DoctorService;

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
public class DoctorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private DoctorController doctorController;

    private ObjectMapper objectMapper;
    private DoctorCreateRequest doctorCreateRequest;
    private DoctorUpdateRequest doctorUpdateRequest;
    private DoctorResponse doctorResponse;
    private DoctorSearchResponse doctorSearchResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDate serialization

        doctorCreateRequest = DoctorCreateRequest.builder()
                .firstName("Amr")
                .lastName("Zedan")
                .email("amr.zedan@hospital.com")
                .password("password123")
                .phoneNumber("+970123456789")
                .dateOfBirth(LocalDate.of(1980, 5, 15))
                .gender(Gender.MALE)
                .address("456 Hospital St")
                .specialization("Cardiology")
                .build();

        doctorUpdateRequest = DoctorUpdateRequest.builder()
                .firstName("Amr")
                .lastName("Zedan")
                .email("amr.zedan@hospital.com")
                .password("password123")
                .phoneNumber("+970123456789")
                .dateOfBirth(LocalDate.of(1980, 5, 15))
                .gender(Gender.MALE)
                .address("456 Hospital St")
                .specialization("Cardiology")
                .status(DoctorStatus.ACTIVE)
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .firstName("Amr")
                .lastName("Zedan")
                .email("amr.zedan@hospital.com")
                .phoneNumber("+970123456789")
                .dateOfBirth(LocalDate.of(1980, 5, 15))
                .gender(Gender.MALE)
                .address("456 Hospital St")
                .roleName("DOCTOR")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        doctorResponse = DoctorResponse.builder()
                .id(1L)
                .user(userResponse)
                .specialization("Cardiology")
                .status(DoctorStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        doctorSearchResponse = DoctorSearchResponse.builder()
                .id(1L)
                .firstName("Amr")
                .lastName("Zedan")
                .specialization("Cardiology")
                .status(DoctorStatus.ACTIVE)
                .phoneNumber("+970123456789")
                .build();
    }

    @Test
    void createDoctor_Success() throws Exception {
        when(doctorService.createDoctor(any(DoctorCreateRequest.class)))
                .thenReturn(doctorResponse);

        mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.firstName").value("Amr"))
                .andExpect(jsonPath("$.user.lastName").value("Zedan"))
                .andExpect(jsonPath("$.user.email").value("amr.zedan@hospital.com"))
                .andExpect(jsonPath("$.specialization").value("Cardiology"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void updateDoctor_Success() throws Exception {
        when(doctorService.updateDoctor(eq(1L), any(DoctorUpdateRequest.class)))
                .thenReturn(doctorResponse);

        mockMvc.perform(put("/api/doctors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.firstName").value("Amr"))
                .andExpect(jsonPath("$.user.lastName").value("Zedan"))
                .andExpect(jsonPath("$.user.email").value("amr.zedan@hospital.com"))
                .andExpect(jsonPath("$.specialization").value("Cardiology"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getDoctorById_Success() throws Exception {
        when(doctorService.getDoctorById(1L))
                .thenReturn(doctorResponse);

        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.firstName").value("Amr"))
                .andExpect(jsonPath("$.user.lastName").value("Zedan"))
                .andExpect(jsonPath("$.user.email").value("amr.zedan@hospital.com"))
                .andExpect(jsonPath("$.specialization").value("Cardiology"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getAllDoctors_Success() throws Exception {
        List<DoctorResponse> doctors = Arrays.asList(doctorResponse);
        when(doctorService.getAllDoctors())
                .thenReturn(doctors);

        mockMvc.perform(get("/api/doctors/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].user.firstName").value("Amr"))
                .andExpect(jsonPath("$[0].user.lastName").value("Zedan"))
                .andExpect(jsonPath("$[0].specialization").value("Cardiology"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void deleteDoctor_Success() throws Exception {
        mockMvc.perform(delete("/api/doctors/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchDoctorsBySpecialty_Success() throws Exception {
        List<DoctorSearchResponse> doctors = Arrays.asList(doctorSearchResponse);
        when(doctorService.searchDoctorsBySpecialty("Cardiology"))
                .thenReturn(doctors);

        mockMvc.perform(get("/api/doctors/search/specialty?specialty=Cardiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Amr"))
                .andExpect(jsonPath("$[0].lastName").value("Zedan"))
                .andExpect(jsonPath("$[0].specialization").value("Cardiology"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void searchDoctorsByName_Success() throws Exception {
        List<DoctorSearchResponse> doctors = Arrays.asList(doctorSearchResponse);
        when(doctorService.searchDoctorsByName("Amr", "Zedan"))
                .thenReturn(doctors);

        mockMvc.perform(get("/api/doctors/search/name?firstName=Amr&lastName=Zedan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Amr"))
                .andExpect(jsonPath("$[0].lastName").value("Zedan"))
                .andExpect(jsonPath("$[0].specialization").value("Cardiology"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
}
