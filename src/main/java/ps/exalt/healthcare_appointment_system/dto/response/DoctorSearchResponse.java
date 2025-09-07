package ps.exalt.healthcare_appointment_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ps.exalt.healthcare_appointment_system.enums.DoctorStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSearchResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private DoctorStatus status;
    private String phoneNumber;
}
