package ps.exalt.healthcare_appointment_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.exalt.healthcare_appointment_system.dto.request.AppointmentBookRequest;
import ps.exalt.healthcare_appointment_system.dto.request.AppointmentCompleteRequest;
import ps.exalt.healthcare_appointment_system.dto.response.AppointmentResponse;
import ps.exalt.healthcare_appointment_system.dto.response.AvailableSlotResponse;
import ps.exalt.healthcare_appointment_system.entity.Appointment;
import ps.exalt.healthcare_appointment_system.entity.Doctor;
import ps.exalt.healthcare_appointment_system.entity.Patient;
import ps.exalt.healthcare_appointment_system.enums.AppointmentStatus;
import ps.exalt.healthcare_appointment_system.exception.InvalidException;
import ps.exalt.healthcare_appointment_system.exception.NotFoundException;
import ps.exalt.healthcare_appointment_system.repository.jpa.AppointmentRepository;
import ps.exalt.healthcare_appointment_system.repository.jpa.DoctorRepository;
import ps.exalt.healthcare_appointment_system.repository.jpa.PatientRepository;
import ps.exalt.healthcare_appointment_system.repository.jpa.DoctorWorkingTimeSlotRepository;
import ps.exalt.healthcare_appointment_system.entity.DoctorWorkingTimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorWorkingTimeSlotRepository doctorWorkingTimeSlotRepository;

    private static final Integer SLOT_DURATION = 30; // 30 minutes

    public AppointmentResponse bookAppointment(Long patientId, AppointmentBookRequest request) {
        // Input validation
        if (patientId == null || patientId <= 0) {
            throw new InvalidException("Invalid patient ID provided");
        }

        if (request == null) {
            throw new InvalidException("Appointment request cannot be null");
        }

        if (request.getDoctorId() == null || request.getDoctorId() <= 0) {
            throw new InvalidException("Invalid doctor ID provided");
        }

        if (request.getSlotId() == null || request.getSlotId() <= 0) {
            throw new InvalidException("Slot ID must be a positive number");
        }

        if (request.getAppointmentDate() == null) {
            throw new InvalidException("Appointment date cannot be null");
        }

        // Get the slot number directly from the request
        int slotNumber = request.getSlotId();

        // Get available slots for the specified date to validate the slot ID
        LocalDate appointmentDate = request.getAppointmentDate();
        AvailableSlotResponse availableSlots = getAvailableSlots(request.getDoctorId(), appointmentDate);

        // Find the requested slot
        if (slotNumber > availableSlots.getAvailableSlots().size()) {
            throw new InvalidException("Invalid slot ID. Slot does not exist.");
        }

        AvailableSlotResponse.TimeSlot selectedSlot = availableSlots.getAvailableSlots().get(slotNumber - 1);

        // Check if the slot is available
        if (!selectedSlot.isAvailable()) {
            throw new InvalidException("Selected time slot is not available.");
        }

        // Validate patient exists
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));

        // Validate doctor exists and is active
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + request.getDoctorId()));

        // Calculate start and end times from selected slot
        LocalDateTime startTime = selectedSlot.getStartTime();
        LocalDateTime endTime = selectedSlot.getEndTime();

        // Check for doctor double-booking
        List<Appointment> doctorOverlappingAppointments = appointmentRepository
                .findOverlappingAppointments(request.getDoctorId(), startTime, endTime);

        if (!doctorOverlappingAppointments.isEmpty()) {
            throw new InvalidException(
                    "This time slot is no longer available. Please refresh and select another slot.");
        }

        // Check for patient double-booking (prevent patient from booking multiple
        // appointments at the same time)
        List<Appointment> patientOverlappingAppointments = appointmentRepository
                .findOverlappingPatientAppointments(patientId, startTime, endTime);

        if (!patientOverlappingAppointments.isEmpty()) {
            throw new InvalidException(
                    "You already have an appointment scheduled at this time. Please select a different time slot.");
        }

        // Create appointment
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(startTime)
                .startTime(startTime)
                .endTime(endTime)
                .durationMinutes(SLOT_DURATION)
                .status(AppointmentStatus.SCHEDULED)
                .notes(request.getNotes())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return convertToAppointmentResponse(savedAppointment);
    }

    public void cancelAppointment(Long patientId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + appointmentId));

        // Verify the appointment belongs to the patient
        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new InvalidException("You can only cancel your own appointments.");
        }

        // Check if appointment can be cancelled
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidException("Cannot cancel a completed appointment.");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidException("Appointment is already cancelled.");
        }

        // Cancel the appointment
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledAt(LocalDateTime.now());

        appointmentRepository.save(appointment);
    }

    public AppointmentResponse completeAppointment(Long doctorId, Long appointmentId,
            AppointmentCompleteRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + appointmentId));

        // Verify the appointment belongs to the doctor
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new InvalidException("You can only complete your own appointments.");
        }

        // Check if appointment can be completed
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidException("Appointment is already completed.");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidException("Cannot complete a cancelled appointment.");
        }

        // Complete the appointment
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setCompletedAt(LocalDateTime.now());

        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            appointment.setNotes(appointment.getNotes() + "\n--- Doctor's Notes ---\n" + request.getNotes());
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToAppointmentResponse(savedAppointment);
    }

    /**
     * Get available time slots for a doctor on a specific date
     */
    @Transactional(readOnly = true)
    public AvailableSlotResponse getAvailableSlots(Long doctorId, LocalDate date) {
        // Validate doctor exists
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + doctorId));

        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Get doctor's working time slots for the specified day
        List<DoctorWorkingTimeSlot> activeSlots = doctorWorkingTimeSlotRepository
                .findByDoctorIdAndDayOfWeekAndIsActiveTrueOrderByStartTime(doctorId, dayOfWeek);

        List<AvailableSlotResponse.TimeSlot> allSlots = new ArrayList<>();

        if (activeSlots.isEmpty()) {
            // Doctor doesn't have any working time slots on this day
            return AvailableSlotResponse.builder()
                    .date(date)
                    .availableSlots(new ArrayList<>())
                    .build();
        }

        // Generate slots for each working time slot
        for (DoctorWorkingTimeSlot workingSlot : activeSlots) {
            List<AvailableSlotResponse.TimeSlot> slotsForThisTimeSlot = generateTimeSlotsForTimeSlot(date,
                    workingSlot);
            allSlots.addAll(slotsForThisTimeSlot);
        }

        // Get existing appointments for the doctor on that date
        LocalDateTime startOfDay = date.atStartOfDay();
        List<Appointment> existingAppointments = appointmentRepository
                .findDoctorAppointmentsByDate(doctorId, startOfDay);

        // Mark slots as unavailable if they overlap with existing appointments
        for (Appointment appointment : existingAppointments) {
            markUnavailableSlots(allSlots, appointment.getStartTime(), appointment.getEndTime());
        }

        // Sort slots by start time
        allSlots.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));

        // Assign sequential slot IDs
        for (int i = 0; i < allSlots.size(); i++) {
            allSlots.get(i).setSlotId(i + 1);
        }

        return AvailableSlotResponse.builder()
                .date(date)
                .availableSlots(allSlots)
                .build();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getPatientAppointments(Long patientId) {
        // Validate patient exists
        patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));

        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return appointments.stream()
                .map(this::convertToAppointmentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getDoctorAppointments(Long doctorId) {
        // Validate doctor exists
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + doctorId));

        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);
        return appointments.stream()
                .map(this::convertToAppointmentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentByIdForPatient(Long appointmentId, Long patientId) {
        // Validate patient exists
        patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + appointmentId));

        // Check if the appointment belongs to the requesting patient
        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new InvalidException("You can only view your own appointments.");
        }

        return convertToAppointmentResponse(appointment);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentByIdForDoctor(Long appointmentId, Long doctorId) {
        // Validate doctor exists
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + doctorId));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + appointmentId));

        // Check if the appointment belongs to the requesting doctor
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new InvalidException("You can only view your own appointments.");
        }

        return convertToAppointmentResponse(appointment);
    }

    private List<AvailableSlotResponse.TimeSlot> generateTimeSlotsForTimeSlot(LocalDate date,
            DoctorWorkingTimeSlot timeSlot) {
        List<AvailableSlotResponse.TimeSlot> slots = new ArrayList<>();
        LocalDateTime current = date.atTime(timeSlot.getStartTime());
        LocalDateTime endOfSlot = date.atTime(timeSlot.getEndTime());

        while (current.isBefore(endOfSlot)) {
            LocalDateTime slotEnd = current.plusMinutes(SLOT_DURATION);
            if (slotEnd.isAfter(endOfSlot)) {
                break;
            }

            slots.add(AvailableSlotResponse.TimeSlot.builder()
                    .startTime(current)
                    .endTime(slotEnd)
                    .available(true)
                    .build());

            current = slotEnd;
        }

        return slots;
    }

    private void markUnavailableSlots(List<AvailableSlotResponse.TimeSlot> slots,
            LocalDateTime appointmentStart,
            LocalDateTime appointmentEnd) {
        for (AvailableSlotResponse.TimeSlot slot : slots) {
            if (slot.getStartTime().isBefore(appointmentEnd) && slot.getEndTime().isAfter(appointmentStart)) {
                slot.setAvailable(false);
            }
        }
    }

    private AppointmentResponse convertToAppointmentResponse(Appointment appointment) {
        if (appointment == null) {
            throw new InvalidException("Appointment cannot be null");
        }

        if (appointment.getPatient() == null || appointment.getPatient().getUser() == null) {
            throw new InvalidException("Appointment patient information is incomplete");
        }

        if (appointment.getDoctor() == null || appointment.getDoctor().getUser() == null) {
            throw new InvalidException("Appointment doctor information is incomplete");
        }

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getUser().getFirstName() + " " +
                        appointment.getPatient().getUser().getLastName())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getUser().getFirstName() + " " +
                        appointment.getDoctor().getUser().getLastName())
                .doctorSpecialization(appointment.getDoctor().getSpecialization())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .durationMinutes(appointment.getDurationMinutes())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}