package kz.citydrive.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kz.citydrive.admin.domain.User;
import kz.citydrive.admin.domain.UserRole;

public class FlutterLoginResponse {

    private String token;
    private Long id;
    private String fullName;
    private String phone;
    private UserRole role;

    @JsonProperty("isApproved")
    private boolean approved;

    public static FlutterLoginResponse from(LoginResponse loginResponse) {
        UserDto user = loginResponse.getUser();
        return fromUserFields(
                user.getId(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(),
                user.isApproved(),
                loginResponse.getToken());
    }

    public static FlutterLoginResponse fromUser(User user, String token) {
        return fromUserFields(
                user.getId(), user.getFullName(), user.getPhone(), user.getRole(), user.isApproved(), token);
    }

    private static FlutterLoginResponse fromUserFields(
            Long id, String fullName, String phone, UserRole role, boolean approved, String token) {
        FlutterLoginResponse response = new FlutterLoginResponse();
        response.setToken(token);
        response.setId(id);
        response.setFullName(fullName);
        response.setPhone(phone);
        response.setRole(role);
        response.setApproved(approved);
        return response;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @JsonProperty("isApproved")
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
