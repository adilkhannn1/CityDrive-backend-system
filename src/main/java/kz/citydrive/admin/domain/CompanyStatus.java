package kz.citydrive.admin.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CompanyStatus {
    DRAFT("draft"),
    PENDING_REVIEW("pending_review"),
    APPROVED("approved"),
    REJECTED("rejected");

    private final String value;

    CompanyStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
