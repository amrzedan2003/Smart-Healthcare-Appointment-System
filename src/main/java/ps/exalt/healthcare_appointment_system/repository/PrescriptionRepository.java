package ps.exalt.healthcare_appointment_system.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ps.exalt.healthcare_appointment_system.entity.Prescription;

import java.util.List;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {
    List<Prescription> findByPatientIdOrderByPrescriptionDateDesc(Long patientId);

    List<Prescription> findByDoctorIdOrderByPrescriptionDateDesc(Long doctorId);

    List<Prescription> findByAppointmentId(Long appointmentId);
}
