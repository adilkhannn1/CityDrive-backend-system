package kz.citydrive.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminRejectApplicationRequest {

    @JsonProperty("admin_note")
    private String adminNote;

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }
}
