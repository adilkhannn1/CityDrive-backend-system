package kz.citydrive.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kz.citydrive.admin.domain.CompanyStatus;

public class CompanyDocumentsResponse {

    private String message;
    private CompanyResponse company;

    public CompanyDocumentsResponse(String message, CompanyResponse company) {
        this.message = message;
        this.company = company;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CompanyResponse getCompany() {
        return company;
    }

    public void setCompany(CompanyResponse company) {
        this.company = company;
    }
}
