package ps.exalt.healthcare_appointment_system.enums;

/**
 * Enumeration for doctor status types
 */
public enum DoctorStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    ON_LEAVE("On Leave");

    private final String displayName;

    DoctorStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAvailable() {
        return this == ACTIVE;
    }
}
