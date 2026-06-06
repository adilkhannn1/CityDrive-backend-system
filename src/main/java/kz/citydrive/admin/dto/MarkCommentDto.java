package kz.citydrive.admin.dto;

import kz.citydrive.admin.domain.MarkComment;
import kz.citydrive.admin.domain.User;

import java.time.Instant;

public class MarkCommentDto {

    private Long id;
    private Long markId;
    private Long authorUserId;
    private String author;
    private String text;
    private Instant createdAt;
    private Integer commentsCount;

    public static MarkCommentDto fromEntity(MarkComment comment, User author) {
        MarkCommentDto dto = new MarkCommentDto();
        dto.setId(comment.getId());
        dto.setMarkId(comment.getMarkId());
        dto.setAuthorUserId(comment.getUserId());
        dto.setAuthor(author != null ? author.getFullName() : "User");
        dto.setText(comment.getText());
        dto.setCreatedAt(comment.getCreatedAt());
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

    public Long getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(Long authorUserId) {
        this.authorUserId = authorUserId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }
}
