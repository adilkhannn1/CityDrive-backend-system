package kz.citydrive.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kz.citydrive.admin.domain.User;

import java.time.Instant;

public class ProfileResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String birthDate;
    private Integer cityId;
    private String cityName;
    private String lang;
    private String role;

    @JsonProperty("is_approved")
    private boolean approved;

    private Instant createdAt;
    private Instant updatedAt;

    public static ProfileResponse fromUser(User user, String cityName, String avatarUrl) {
        ProfileResponse response = new ProfileResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setAvatarUrl(avatarUrl != null ? avatarUrl : user.getAvatarUrl());
        response.setBirthDate(user.getBirthDate());
        response.setCityId(user.getCityId());
        response.setCityName(cityName);
        response.setLang(user.getLang() != null ? user.getLang() : "ru");
        response.setRole(user.getRole() != null ? user.getRole().name() : null);
        response.setApproved(user.isApproved());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("is_approved")
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
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
