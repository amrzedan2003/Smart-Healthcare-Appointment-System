package ps.exalt.healthcare_appointment_system.enums;

/**
 * Enumeration for user gender types
 */
public enum Gender {
    MALE("Male"),
    FEMALE("Female");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
