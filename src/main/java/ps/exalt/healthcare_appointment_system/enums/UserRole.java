package ps.exalt.healthcare_appointment_system.enums;

/**
 * Enumeration for user roles in the system
 */
public enum UserRole {
    ADMIN("Administrator"),
    DOCTOR("Doctor"),
    PATIENT("Patient");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public boolean canAccessPatientData() {
        return this == ADMIN || this == DOCTOR;
    }

    public boolean canManageAppointments() {
        return this == ADMIN || this == DOCTOR;
    }

    public boolean canAccessFinancialData() {
        return this == ADMIN;
    }

    public boolean hasAdministrativeAccess() {
        return this == ADMIN;
    }
}
