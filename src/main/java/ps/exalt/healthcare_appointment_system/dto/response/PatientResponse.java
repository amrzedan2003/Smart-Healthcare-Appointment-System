package ps.exalt.healthcare_appointment_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ps.exalt.healthcare_appointment_system.enums.BloodType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {
    private Long id;
    private UserResponse user;
    private BloodType bloodType;
    private Integer heightCm;
    private Double weightKg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
