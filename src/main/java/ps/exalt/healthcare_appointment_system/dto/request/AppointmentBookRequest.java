package ps.exalt.healthcare_appointment_system.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentBookRequest {
    @NotNull(message = "Doctor ID is required")
    @Positive(message = "Doctor ID must be positive")
    private Long doctorId;

    @NotNull(message = "Slot ID is required")
    @Positive(message = "Slot ID must be positive")
    private Integer slotId;

    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    private String notes;
}
