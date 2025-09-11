package ps.exalt.healthcare_appointment_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponse {
    private String id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private Long appointmentId;
    private String notes;
    private List<String> medicines;
    private String labResults;
    private LocalDateTime prescriptionDate;
    private LocalDateTime createdAt;
}
