package kz.citydrive.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kz.citydrive.admin.domain.CompanyStatus;

public class CompanyRegistrationStateResponse {

    private String step;

    @JsonProperty("is_approved")
    private boolean approved;

    @JsonProperty("company_status")
    private CompanyStatus companyStatus;

    @JsonProperty("rejection_reason")
    private String rejectionReason;

    public CompanyRegistrationStateResponse(
            String step, boolean approved, CompanyStatus companyStatus, String rejectionReason) {
        this.step = step;
        this.approved = approved;
        this.companyStatus = companyStatus;
        this.rejectionReason = rejectionReason;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    @JsonProperty("is_approved")
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public CompanyStatus getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(CompanyStatus companyStatus) {
        this.companyStatus = companyStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
