package kz.citydrive.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "support_settings")
public class SupportSettings {

    @Id
    private Long id = 1L;

    @Column(name = "whatsapp_phone", nullable = false, length = 32)
    private String whatsappPhone;

    @Column(name = "whatsapp_message", nullable = false, length = 500)
    private String whatsappMessage;

    @Column(nullable = false, length = 128)
    private String email;

    @Column(nullable = false, length = 32)
    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
