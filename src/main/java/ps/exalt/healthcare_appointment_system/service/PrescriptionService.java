package ps.exalt.healthcare_appointment_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ps.exalt.healthcare_appointment_system.dto.request.PrescriptionCreateRequest;
import ps.exalt.healthcare_appointment_system.dto.response.PrescriptionResponse;
import ps.exalt.healthcare_appointment_system.entity.Appointment;
import ps.exalt.healthcare_appointment_system.entity.Doctor;
import ps.exalt.healthcare_appointment_system.entity.Patient;
import ps.exalt.healthcare_appointment_system.entity.Prescription;
import ps.exalt.healthcare_appointment_system.exception.NotFoundException;
import ps.exalt.healthcare_appointment_system.repository.jpa.AppointmentRepository;
import ps.exalt.healthcare_appointment_system.repository.jpa.DoctorRepository;
import ps.exalt.healthcare_appointment_system.repository.jpa.PatientRepository;
import ps.exalt.healthcare_appointment_system.repository.mongodb.PrescriptionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

        private final PrescriptionRepository prescriptionRepository;
        private final AppointmentRepository appointmentRepository;
        private final PatientRepository patientRepository;
        private final DoctorRepository doctorRepository;

        public PrescriptionResponse createPrescription(PrescriptionCreateRequest request, String doctorEmail) {
                // Get doctor from authenticated email
                Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                                .orElseThrow(() -> new NotFoundException("Doctor not found"));

                // Verify appointment exists and belongs to the doctor
                Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                                .orElseThrow(() -> new NotFoundException("Appointment not found"));

                if (!appointment.getDoctor().getId().equals(doctor.getId())) {
                        throw new RuntimeException("You can only create prescriptions for your own appointments");
                }

                // Get patient details
                Patient patient = appointment.getPatient();

                // Create prescription
                Prescription prescription = Prescription.builder()
                                .patientId(patient.getId())
                                .patientName(patient.getUser().getFirstName() + " " + patient.getUser().getLastName())
                                .doctorId(doctor.getId())
                                .doctorName(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName())
                                .doctorSpecialization(doctor.getSpecialization())
                                .appointmentId(appointment.getId())
                                .notes(request.getNotes())
                                .medicines(request.getMedicines())
                                .labResults(request.getLabResults())
                                .build();

                Prescription savedPrescription = prescriptionRepository.save(prescription);

                return mapToResponse(savedPrescription);
        }

        public List<PrescriptionResponse> getPatientPrescriptions(Long patientId) {
                // Verify patient exists
                patientRepository.findById(patientId)
                                .orElseThrow(() -> new NotFoundException("Patient not found"));

                List<Prescription> prescriptions = prescriptionRepository
                                .findByPatientIdOrderByPrescriptionDateDesc(patientId);

                return prescriptions.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        public List<PrescriptionResponse> getMyPrescriptions(String patientEmail) {
                // Get patient from authenticated email
                Patient patient = patientRepository.findByUserEmail(patientEmail)
                                .orElseThrow(() -> new NotFoundException("Patient not found"));

                List<Prescription> prescriptions = prescriptionRepository
                                .findByPatientIdOrderByPrescriptionDateDesc(patient.getId());

                return prescriptions.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        public List<PrescriptionResponse> getMyDoctorPrescriptions(String doctorEmail) {
                // Get doctor from authenticated email
                Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                                .orElseThrow(() -> new NotFoundException("Doctor not found"));

                List<Prescription> prescriptions = prescriptionRepository
                                .findByDoctorIdOrderByPrescriptionDateDesc(doctor.getId());

                return prescriptions.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        public PrescriptionResponse getPrescriptionById(String prescriptionId) {
                Prescription prescription = prescriptionRepository.findById(prescriptionId)
                                .orElseThrow(() -> new NotFoundException("Prescription not found"));

                return mapToResponse(prescription);
        }

        private PrescriptionResponse mapToResponse(Prescription prescription) {
                return PrescriptionResponse.builder()
                                .id(prescription.getId())
                                .patientId(prescription.getPatientId())
                                .patientName(prescription.getPatientName())
                                .doctorId(prescription.getDoctorId())
                                .doctorName(prescription.getDoctorName())
                                .doctorSpecialization(prescription.getDoctorSpecialization())
                                .appointmentId(prescription.getAppointmentId())
                                .notes(prescription.getNotes())
                                .medicines(prescription.getMedicines())
                                .labResults(prescription.getLabResults())
                                .prescriptionDate(prescription.getPrescriptionDate())
                                .createdAt(prescription.getCreatedAt())
                                .build();
        }
}
