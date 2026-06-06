package kz.citydrive.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegisterVerifyRequest {

    @NotBlank(message = "phone is required")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "phone must be in format +77001234567")
    private String phone;

    @NotBlank(message = "code is required")
    private String code;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
