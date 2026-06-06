package kz.citydrive.admin.dto;

import kz.citydrive.admin.domain.MarkComment;
import kz.citydrive.admin.domain.User;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Комментарий с телефоном автора — для админ-панели */
public class MarkCommentAdminDto {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());

    private Long id;
    private Long markId;
    private Long authorUserId;
    private String author;
    private String authorPhone;
    private String text;
    private Instant createdAt;
    private String createdAtText;

    public static MarkCommentAdminDto fromEntity(MarkComment comment, User author) {
        MarkCommentAdminDto dto = new MarkCommentAdminDto();
        dto.setId(comment.getId());
        dto.setMarkId(comment.getMarkId());
        dto.setAuthorUserId(comment.getUserId());
        dto.setAuthor(author != null ? author.getFullName() : "User");
        dto.setAuthorPhone(author != null ? author.getPhone() : "—");
        dto.setText(comment.getText());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setCreatedAtText(formatInstant(comment.getCreatedAt()));
        return dto;
    }

    private static String formatInstant(Instant instant) {
        return instant != null ? DISPLAY_FORMAT.format(instant) : "—";
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

    public String getAuthorPhone() {
        return authorPhone;
    }

    public void setAuthorPhone(String authorPhone) {
        this.authorPhone = authorPhone;
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

    public String getCreatedAtText() {
        return createdAtText;
    }

    public void setCreatedAtText(String createdAtText) {
        this.createdAtText = createdAtText;
    }
}
