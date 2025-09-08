package ps.exalt.healthcare_appointment_system.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ps.exalt.healthcare_appointment_system.enums.BloodType;
import ps.exalt.healthcare_appointment_system.enums.Gender;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientUpdateRequest {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 15, message = "First name must be between 2 and 15 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 15, message = "Last name must be between 2 and 15 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @Pattern(regexp = "^\\+970[0-9]{9}$", message = "Phone number must be in Palestinian format: +970xxxxxxxxx")
    private String phoneNumber;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    private BloodType bloodType;

    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 200, message = "Height cannot exceed 200 cm")
    private Integer heightCm;

    @DecimalMin(value = "1.0", message = "Weight must be at least 1 kg")
    @DecimalMax(value = "500.0", message = "Weight cannot exceed 500 kg")
    @Positive(message = "Weight must be a positive number")
    private Double weightKg;
}