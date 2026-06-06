package kz.citydrive.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kz.citydrive.admin.domain.UserRole;

public class RegisterRequest {

    @NotBlank(message = "full_name is required")
    private String fullName;

    @NotBlank(message = "phone is required")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "phone must be in format +77001234567")
    private String phone;

    @NotNull(message = "city_id is required")
    private Integer cityId;

    @NotBlank(message = "birth_date is required")
    private String birthDate;

    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be at least 6 characters")
    private String password;

    @NotBlank(message = "password_confirmation is required")
    private String passwordConfirmation;

    /** Роль: RESIDENT (по умолчанию) или CONTROLLER (требует подтверждения админом). */
    private UserRole role = UserRole.RESIDENT;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public UserRole getRole() {
        return role != null ? role : UserRole.RESIDENT;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
