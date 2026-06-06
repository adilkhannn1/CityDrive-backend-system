package kz.citydrive.admin.dto;

public class RegisterInitResponse {

    private String message;
    private String phone;
    private String fullName;

    public RegisterInitResponse(String message, String phone, String fullName) {
        this.message = message;
        this.phone = phone;
        this.fullName = fullName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
