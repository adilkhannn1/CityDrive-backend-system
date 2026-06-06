package kz.citydrive.admin.dto;

public class AssignedControllerDto {

    private Long id;
    private String fullName;
    private String companyName;

    public AssignedControllerDto(Long id, String fullName, String companyName) {
        this.id = id;
        this.fullName = fullName;
        this.companyName = companyName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
