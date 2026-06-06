package kz.citydrive.admin.dto;

public class StatusUpdateRequest {

    private String status;
    private Long assignedControllerId;
    private String adminNote;
    private String comment;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAssignedControllerId() {
        return assignedControllerId;
    }

    public void setAssignedControllerId(Long assignedControllerId) {
        this.assignedControllerId = assignedControllerId;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
