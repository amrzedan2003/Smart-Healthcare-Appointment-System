package ps.exalt.healthcare_appointment_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotResponse {
    private LocalDate date;
    private List<TimeSlot> availableSlots;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlot {
        private Integer slotId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private boolean available;
    }
}
