package ps.exalt.healthcare_appointment_system.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionCreateRequest {
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    private String notes;

    private List<String> medicines;

    private String labResults;
}
