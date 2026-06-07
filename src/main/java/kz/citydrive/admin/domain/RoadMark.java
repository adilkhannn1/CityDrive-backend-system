package kz.citydrive.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "road_marks")
public class RoadMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long authorUserId;

    private Long assignedControllerId;

    private String title;

    @Column(length = 4000)
    private String description;

    private String address;

    private Double lat;

    private Double lng;

    private String type;

    private String severity;

    @Column(nullable = false)
    private MarkStatus status = MarkStatus.NEW;

    private Instant reportedDate;

    @Column(length = 8000)
    private String imagesJson = "[]";

    private String author;

    private Integer likes = 0;

    private Integer commentsCount = 0;

    @Column(length = 2000)
    private String adminNote;

    @Column(name = "controller_comment", length = 2000)
    private String controllerComment;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "work_report_description", length = 4000)
    private String workReportDescription;

    @Column(name = "work_report_images_json", length = 8000)
    private String workReportImagesJson = "[]";

    @Column(name = "work_started_at")
    private Instant workStartedAt;

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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
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

    public MarkStatus getStatus() {
        return status;
    }

    public void setStatus(MarkStatus status) {
        this.status = status;
    }

    public Instant getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(Instant reportedDate) {
        this.reportedDate = reportedDate;
    }

    public String getImagesJson() {
        return imagesJson;
    }

    public void setImagesJson(String imagesJson) {
        this.imagesJson = imagesJson;
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

    public String getWorkReportDescription() {
        return workReportDescription;
    }

    public void setWorkReportDescription(String workReportDescription) {
        this.workReportDescription = workReportDescription;
    }

    public String getWorkReportImagesJson() {
        return workReportImagesJson;
    }

    public void setWorkReportImagesJson(String workReportImagesJson) {
        this.workReportImagesJson = workReportImagesJson;
    }

    public Instant getWorkStartedAt() {
        return workStartedAt;
    }

    public void setWorkStartedAt(Instant workStartedAt) {
        this.workStartedAt = workStartedAt;
    }
}
