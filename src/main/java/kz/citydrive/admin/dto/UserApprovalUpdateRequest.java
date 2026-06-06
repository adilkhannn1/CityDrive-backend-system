package kz.citydrive.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserApprovalUpdateRequest {

    @JsonProperty("isApproved")
    private boolean approved;

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
