package kz.citydrive.admin.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.citydrive.admin.domain.RoadMark;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class RoadMarkDto {

    private Long id;
    private Long authorUserId;
    private Long assignedControllerId;
    private String title;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private String type;
    private String severity;
    private String status;
    private Instant reportedDate;
    private List<String> images;
    private String author;
    private Integer likes;
    private Integer commentsCount;
    private String adminNote;
    private Boolean likedByMe;
    private MarkCommentDto latestComment;

    @com.fasterxml.jackson.annotation.JsonProperty("controller_comment")
    private String controllerComment;

    @com.fasterxml.jackson.annotation.JsonProperty("accepted_at")
    private Instant acceptedAt;

    @com.fasterxml.jackson.annotation.JsonProperty("assigned_controller")
    private AssignedControllerDto assignedController;

    public static RoadMarkDto fromEntity(RoadMark mark, ObjectMapper objectMapper) {
        RoadMarkDto dto = new RoadMarkDto();
        dto.setId(mark.getId());
        dto.setAuthorUserId(mark.getAuthorUserId());
        dto.setAssignedControllerId(mark.getAssignedControllerId());
        dto.setTitle(mark.getTitle());
        dto.setDescription(mark.getDescription());
        dto.setAddress(mark.getAddress());
        dto.setLatitude(mark.getLat());
        dto.setLongitude(mark.getLng());
        dto.setType(mark.getType());
        dto.setSeverity(mark.getSeverity());
        dto.setStatus(mark.getStatus() != null ? mark.getStatus().getValue() : null);
        dto.setReportedDate(mark.getReportedDate());
        dto.setImages(parseImages(mark.getImagesJson(), objectMapper));
        dto.setAuthor(mark.getAuthor());
        dto.setLikes(mark.getLikes());
        dto.setCommentsCount(mark.getCommentsCount());
        dto.setAdminNote(mark.getAdminNote());
        dto.setControllerComment(mark.getControllerComment());
        dto.setAcceptedAt(mark.getAcceptedAt());
        return dto;
    }

    private static List<String> parseImages(String json, ObjectMapper objectMapper) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(Long authorUserId) {
        this.authorUserId = authorUserId;
    }

    public Long getAssignedControllerId() {
        return assignedControllerId;
    }

    public void setAssignedControllerId(Long assignedControllerId) {
        this.assignedControllerId = assignedControllerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(Instant reportedDate) {
        this.reportedDate = reportedDate;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public Boolean getLikedByMe() {
        return likedByMe;
    }

    public void setLikedByMe(Boolean likedByMe) {
        this.likedByMe = likedByMe;
    }

    public MarkCommentDto getLatestComment() {
        return latestComment;
    }

    public void setLatestComment(MarkCommentDto latestComment) {
        this.latestComment = latestComment;
    }

    public String getControllerComment() {
        return controllerComment;
    }

    public void setControllerComment(String controllerComment) {
        this.controllerComment = controllerComment;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public AssignedControllerDto getAssignedController() {
        return assignedController;
    }

    public void setAssignedController(AssignedControllerDto assignedController) {
        this.assignedController = assignedController;
    }
}
