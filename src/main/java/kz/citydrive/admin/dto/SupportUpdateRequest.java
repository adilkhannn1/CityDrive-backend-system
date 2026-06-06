package kz.citydrive.admin.dto;

public class SupportUpdateRequest {

    private String whatsappPhone;
    private String whatsappMessage;
    private String email;
    private String phone;

    public String getWhatsappPhone() {
        return whatsappPhone;
    }

    public void setWhatsappPhone(String whatsappPhone) {
        this.whatsappPhone = whatsappPhone;
    }

    public String getWhatsappMessage() {
        return whatsappMessage;
    }

    public void setWhatsappMessage(String whatsappMessage) {
        this.whatsappMessage = whatsappMessage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
