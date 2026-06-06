package kz.citydrive.admin.dto;

import kz.citydrive.admin.domain.MarkLike;
import kz.citydrive.admin.domain.User;

import java.time.Instant;

public class MarkLikeDto {

    private Long id;
    private Long markId;
    private Long userId;
    private String author;
    private Instant createdAt;

    public static MarkLikeDto fromEntity(MarkLike like, User user) {
        MarkLikeDto dto = new MarkLikeDto();
        dto.setId(like.getId());
        dto.setMarkId(like.getMarkId());
        dto.setUserId(like.getUserId());
        dto.setAuthor(user != null ? user.getFullName() : "User");
        dto.setCreatedAt(like.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMarkId() {
        return markId;
    }

    public void setMarkId(Long markId) {
        this.markId = markId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
