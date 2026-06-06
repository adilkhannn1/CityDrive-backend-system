package kz.citydrive.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kz.citydrive.admin.domain.Company;
import kz.citydrive.admin.domain.CompanyStatus;

import java.time.Instant;

public class CompanyResponse {

    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    private String name;
    private String bin;

    @JsonProperty("legal_address")
    private String legalAddress;

    @JsonProperty("founded_year")
    private int foundedYear;

    private CompanyStatus status;

    @JsonProperty("registration_certificate_url")
    private String registrationCertificateUrl;

    @JsonProperty("portfolio_url")
    private String portfolioUrl;

    @JsonProperty("rejection_reason")
    private String rejectionReason;

    @JsonProperty("submitted_at")
    private Instant submittedAt;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    public static CompanyResponse fromEntity(Company company) {
        CompanyResponse response = new CompanyResponse();
        response.setId(company.getId());
        response.setUserId(company.getUserId());
        response.setName(company.getName());
        response.setBin(company.getBin());
        response.setLegalAddress(company.getLegalAddress());
        response.setFoundedYear(company.getFoundedYear());
        response.setStatus(company.getStatus());
        response.setRegistrationCertificateUrl(company.getRegistrationCertificateUrl());
        response.setPortfolioUrl(company.getPortfolioUrl());
        response.setRejectionReason(company.getRejectionReason());
        response.setSubmittedAt(company.getSubmittedAt());
        response.setCreatedAt(company.getCreatedAt());
        response.setUpdatedAt(company.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getLegalAddress() {
        return legalAddress;
    }

    public void setLegalAddress(String legalAddress) {
        this.legalAddress = legalAddress;
    }

    public int getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(int foundedYear) {
        this.foundedYear = foundedYear;
    }

    public CompanyStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
    }

    public String getRegistrationCertificateUrl() {
        return registrationCertificateUrl;
    }

    public void setRegistrationCertificateUrl(String registrationCertificateUrl) {
        this.registrationCertificateUrl = registrationCertificateUrl;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
