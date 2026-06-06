package kz.citydrive.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kz.citydrive.admin.domain.User;
import kz.citydrive.admin.domain.UserRole;

public class UserDto {

    private Long id;
    private String fullName;
    private String phone;
    private UserRole role;
    private boolean blocked;

    @JsonProperty("isApproved")
    private boolean approved;

    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setBlocked(user.isBlocked());
        dto.setApproved(user.isApproved());
        return dto;
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

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @JsonProperty("isApproved")
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
