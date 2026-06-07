package kz.citydrive.admin.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MarkStatus {
    NEW("new"),
    PENDING("pending"),
    CONFIRMED("confirmed"),
    CONTROLLER_ASSIGNED("controller_assigned"),
    REJECTED("rejected"),
    IN_PROGRESS("in_progress"),
    FIXED("fixed");

    private final String value;

    MarkStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static MarkStatus fromValue(String value) {
        if (value == null || value.isBlank()) {
            return NEW;
        }
        for (MarkStatus status : values()) {
            if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown mark status: " + value);
    }
}
