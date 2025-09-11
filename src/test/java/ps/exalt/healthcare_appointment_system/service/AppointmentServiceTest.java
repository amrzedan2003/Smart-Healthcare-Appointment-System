package ps.exalt.healthcare_appointment_system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ps.exalt.healthcare_appointment_system.dto.request.AppointmentBookRequest;
import ps.exalt.healthcare_appointment_system.dto.response.AppointmentResponse;
import ps.exalt.healthcare_appointment_system.dto.response.AvailableSlotResponse;
import ps.exalt.healthcare_appointment_system.entity.Appointment;
import ps.exalt.healthcare_appointment_system.entity.Doctor;
import ps.exalt.healthcare_appointment_system.entity.Patient;
import ps.exalt.healthcare_appointment_system.entity.User;
import ps.exalt.healthcare_appointment_system.enums.AppointmentStatus;
import ps.exalt.healthcare_appointment_system.exception.InvalidException;
import ps.exalt.healthcare_appointment_system.exception.NotFoundException;
import ps.exalt.healthcare_appointment_system.repository.jpa.AppointmentRepository;
import ps.exalt.healthcare_appointment_system.repository.jpa.DoctorRepository;
import ps.exalt.healthcare_appointment_system.repository.jpa.PatientRepository;
import ps.exalt.healthcare_appointment_system.repository.jpa.DoctorWorkingTimeSlotRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

        @Mock
        private AppointmentRepository appointmentRepository;

        @Mock
        private PatientRepository patientRepository;

        @Mock
        private DoctorRepository doctorRepository;

        @Mock
        private DoctorWorkingTimeSlotRepository doctorWorkingTimeSlotRepository;

        @InjectMocks
        private AppointmentService appointmentService;

        private Patient mockPatient;
        private Doctor mockDoctor;
        private User mockPatientUser;
        private User mockDoctorUser;
        private AppointmentBookRequest bookRequest;

        @BeforeEach
        void setUp() {
                // Create mock users
                mockPatientUser = User.builder()
                                .id(1L)
                                .firstName("Amr")
                                .lastName("Zedan")
                                .email("amr.zedan@test.com")
                                .build();

                mockDoctorUser = User.builder()
                                .id(2L)
                                .firstName("Hamza")
                                .lastName("Barabrah")
                                .email("hamza.barabrah@test.com")
                                .build();

                // Create mock patient and doctor
                mockPatient = Patient.builder()
                                .id(1L)
                                .user(mockPatientUser)
                                .build();

                mockDoctor = Doctor.builder()
                                .id(1L)
                                .user(mockDoctorUser)
                                .specialization("Cardiology")
                                .build();

                // Create booking request
                bookRequest = AppointmentBookRequest.builder()
                                .doctorId(1L)
                                .slotId(1)
                                .appointmentDate(LocalDate.now().plusDays(1))
                                .notes("Test appointment")
                                .build();
        }

        @Test
        void bookAppointment_Success() {
                // Given
                String patientEmail = "amr.zedan@test.com";
                LocalDateTime startTime = LocalDate.now().plusDays(1).atTime(9, 0);
                LocalDateTime endTime = startTime.plusMinutes(30);

                // Create available slot response
                AvailableSlotResponse.TimeSlot timeSlot = AvailableSlotResponse.TimeSlot.builder()
                                .slotId(1)
                                .startTime(startTime)
                                .endTime(endTime)
                                .available(true)
                                .build();

                AvailableSlotResponse availableSlots = AvailableSlotResponse.builder()
                                .date(bookRequest.getAppointmentDate())
                                .availableSlots(List.of(timeSlot))
                                .build();

                // Create saved appointment
                Appointment savedAppointment = Appointment.builder()
                                .id(1L)
                                .patient(mockPatient)
                                .doctor(mockDoctor)
                                .appointmentDate(startTime)
                                .startTime(startTime)
                                .endTime(endTime)
                                .status(AppointmentStatus.SCHEDULED)
                                .build();

                // Mock repository calls
                when(patientRepository.findByUserEmail(patientEmail)).thenReturn(Optional.of(mockPatient));
                when(doctorRepository.findById(1L)).thenReturn(Optional.of(mockDoctor));
                when(appointmentRepository.findOverlappingAppointments(1L, startTime, endTime))
                                .thenReturn(new ArrayList<>());
                when(appointmentRepository.findOverlappingPatientAppointments(1L, startTime, endTime))
                                .thenReturn(new ArrayList<>());
                when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

                // Mock getAvailableSlots using spy
                AppointmentService spyService = spy(appointmentService);
                doReturn(availableSlots).when(spyService).getAvailableSlots(1L, bookRequest.getAppointmentDate());

                // When
                AppointmentResponse response = spyService.bookAppointment(bookRequest, patientEmail);

                // Then
                assertNotNull(response);
                assertEquals(1L, response.getId());
                assertEquals(AppointmentStatus.SCHEDULED, response.getStatus());
                verify(appointmentRepository).save(any(Appointment.class));
        }

        @Test
        void bookAppointment_PatientNotFound_ThrowsNotFoundException() {
                // Given
                String patientEmail = "kareem.qutob@test.com";
                when(patientRepository.findByUserEmail(patientEmail)).thenReturn(Optional.empty());

                // When & Then
                assertThrows(NotFoundException.class,
                                () -> appointmentService.bookAppointment(bookRequest, patientEmail));
        }

        @Test
        void bookAppointment_DoctorDoubleBooking_ThrowsInvalidException() {
                // Given
                String patientEmail = "amr.zedan@test.com";
                LocalDateTime startTime = LocalDate.now().plusDays(1).atTime(9, 0);
                LocalDateTime endTime = startTime.plusMinutes(30);

                // Create available slot response
                AvailableSlotResponse.TimeSlot timeSlot = AvailableSlotResponse.TimeSlot.builder()
                                .slotId(1)
                                .startTime(startTime)
                                .endTime(endTime)
                                .available(true)
                                .build();

                AvailableSlotResponse availableSlots = AvailableSlotResponse.builder()
                                .date(bookRequest.getAppointmentDate())
                                .availableSlots(List.of(timeSlot))
                                .build();

                // Create existing appointment (overlapping)
                Appointment existingAppointment = Appointment.builder()
                                .id(999L)
                                .doctor(mockDoctor)
                                .startTime(startTime)
                                .endTime(endTime)
                                .status(AppointmentStatus.SCHEDULED)
                                .build();

                // Mock repository calls
                when(patientRepository.findByUserEmail(patientEmail)).thenReturn(Optional.of(mockPatient));
                when(doctorRepository.findById(1L)).thenReturn(Optional.of(mockDoctor));
                when(appointmentRepository.findOverlappingAppointments(1L, startTime, endTime))
                                .thenReturn(List.of(existingAppointment));

                // Mock getAvailableSlots using spy
                AppointmentService spyService = spy(appointmentService);
                doReturn(availableSlots).when(spyService).getAvailableSlots(1L, bookRequest.getAppointmentDate());

                // When & Then
                InvalidException exception = assertThrows(InvalidException.class,
                                () -> spyService.bookAppointment(bookRequest, patientEmail));

                assertTrue(exception.getMessage().contains("no longer available"));
                verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        void bookAppointment_PatientDoubleBooking_ThrowsInvalidException() {
                // Given
                String patientEmail = "amr.zedan@test.com";
                LocalDateTime startTime = LocalDate.now().plusDays(1).atTime(9, 0);
                LocalDateTime endTime = startTime.plusMinutes(30);

                // Create available slot response
                AvailableSlotResponse.TimeSlot timeSlot = AvailableSlotResponse.TimeSlot.builder()
                                .slotId(1)
                                .startTime(startTime)
                                .endTime(endTime)
                                .available(true)
                                .build();

                AvailableSlotResponse availableSlots = AvailableSlotResponse.builder()
                                .date(bookRequest.getAppointmentDate())
                                .availableSlots(List.of(timeSlot))
                                .build();

                // Create existing patient appointment (overlapping)
                Appointment existingPatientAppointment = Appointment.builder()
                                .id(888L)
                                .patient(mockPatient)
                                .startTime(startTime)
                                .endTime(endTime)
                                .status(AppointmentStatus.SCHEDULED)
                                .build();

                // Mock repository calls
                when(patientRepository.findByUserEmail(patientEmail)).thenReturn(Optional.of(mockPatient));
                when(doctorRepository.findById(1L)).thenReturn(Optional.of(mockDoctor));
                when(appointmentRepository.findOverlappingAppointments(1L, startTime, endTime))
                                .thenReturn(new ArrayList<>());
                when(appointmentRepository.findOverlappingPatientAppointments(1L, startTime, endTime))
                                .thenReturn(List.of(existingPatientAppointment));

                // Mock getAvailableSlots using spy
                AppointmentService spyService = spy(appointmentService);
                doReturn(availableSlots).when(spyService).getAvailableSlots(1L, bookRequest.getAppointmentDate());

                // When & Then
                InvalidException exception = assertThrows(InvalidException.class,
                                () -> spyService.bookAppointment(bookRequest, patientEmail));

                assertTrue(exception.getMessage().contains("already have an appointment"));
                verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        void bookAppointment_SlotNotAvailable_ThrowsInvalidException() {
                // Given
                String patientEmail = "amr.zedan@test.com";
                LocalDateTime startTime = LocalDate.now().plusDays(1).atTime(9, 0);
                LocalDateTime endTime = startTime.plusMinutes(30);

                // Create unavailable slot response
                AvailableSlotResponse.TimeSlot timeSlot = AvailableSlotResponse.TimeSlot.builder()
                                .slotId(1)
                                .startTime(startTime)
                                .endTime(endTime)
                                .available(false) // Slot is not available
                                .build();

                AvailableSlotResponse availableSlots = AvailableSlotResponse.builder()
                                .date(bookRequest.getAppointmentDate())
                                .availableSlots(List.of(timeSlot))
                                .build();

                // Mock repository calls
                when(patientRepository.findByUserEmail(patientEmail)).thenReturn(Optional.of(mockPatient));

                // Mock getAvailableSlots using spy
                AppointmentService spyService = spy(appointmentService);
                doReturn(availableSlots).when(spyService).getAvailableSlots(1L, bookRequest.getAppointmentDate());

                // When & Then
                InvalidException exception = assertThrows(InvalidException.class,
                                () -> spyService.bookAppointment(bookRequest, patientEmail));

                assertTrue(exception.getMessage().contains("not available"));
                verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        void cancelAppointment_Success() {
                // Given
                Long appointmentId = 1L;
                String patientEmail = "amr.zedan@test.com";

                Appointment appointment = Appointment.builder()
                                .id(appointmentId)
                                .patient(mockPatient)
                                .doctor(mockDoctor)
                                .status(AppointmentStatus.SCHEDULED)
                                .build();

                when(patientRepository.findByUserEmail(patientEmail)).thenReturn(Optional.of(mockPatient));
                when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

                // When
                appointmentService.cancelAppointment(appointmentId, patientEmail);

                // Then
                assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
                assertNotNull(appointment.getCancelledAt());
                verify(appointmentRepository).save(appointment);
        }

        @Test
        void cancelAppointment_AppointmentNotFound_ThrowsNotFoundException() {
                // Given
                Long appointmentId = 999L;
                String patientEmail = "amr.zedan@test.com";

                when(patientRepository.findByUserEmail(patientEmail)).thenReturn(Optional.of(mockPatient));
                when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

                // When & Then
                assertThrows(NotFoundException.class,
                                () -> appointmentService.cancelAppointment(appointmentId, patientEmail));
        }

        @Test
        void cancelAppointment_NotPatientAppointment_ThrowsInvalidException() {
                // Given
                Long appointmentId = 1L;
                String patientEmail = "amr.zedan@test.com";

                // Create different patient
                Patient differentPatient = Patient.builder()
                                .id(999L)
                                .user(User.builder().id(999L).firstName("Kareem").lastName("Qutob")
                                                .email("kareem.qutob@test.com").build())
                                .build();

                Appointment appointment = Appointment.builder()
                                .id(appointmentId)
                                .patient(differentPatient) // Different patient
                                .doctor(mockDoctor)
                                .status(AppointmentStatus.SCHEDULED)
                                .build();

                when(patientRepository.findByUserEmail(patientEmail)).thenReturn(Optional.of(mockPatient));
                when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

                // When & Then
                InvalidException exception = assertThrows(InvalidException.class,
                                () -> appointmentService.cancelAppointment(appointmentId, patientEmail));

                assertTrue(exception.getMessage().contains("can only cancel your own"));
        }

        @Test
        void cancelAppointment_AlreadyCancelled_ThrowsInvalidException() {
                // Given
                Long appointmentId = 1L;
                String patientEmail = "amr.zedan@test.com";

                Appointment appointment = Appointment.builder()
                                .id(appointmentId)
                                .patient(mockPatient)
                                .doctor(mockDoctor)
                                .status(AppointmentStatus.CANCELLED) // Already cancelled
                                .build();

                when(patientRepository.findByUserEmail(patientEmail)).thenReturn(Optional.of(mockPatient));
                when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

                // When & Then
                InvalidException exception = assertThrows(InvalidException.class,
                                () -> appointmentService.cancelAppointment(appointmentId, patientEmail));

                assertTrue(exception.getMessage().contains("already cancelled"));
        }
}
