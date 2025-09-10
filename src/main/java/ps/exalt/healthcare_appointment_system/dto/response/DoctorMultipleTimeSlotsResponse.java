package ps.exalt.healthcare_appointment_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorMultipleTimeSlotsResponse {
    private Long doctorId;
    private DayOfWeek dayOfWeek;
    private List<TimeSlotResponse> timeSlots;
}
