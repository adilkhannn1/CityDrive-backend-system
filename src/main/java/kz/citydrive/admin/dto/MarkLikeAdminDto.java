package kz.citydrive.admin.dto;

import kz.citydrive.admin.domain.MarkLike;
import kz.citydrive.admin.domain.User;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Лайк с телефоном пользователя — для админ-панели */
public class MarkLikeAdminDto {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());

    private Long id;
    private Long markId;
    private Long userId;
    private String author;
    private String phone;
    private Instant createdAt;
    private String createdAtText;

    public static MarkLikeAdminDto fromEntity(MarkLike like, User user) {
        MarkLikeAdminDto dto = new MarkLikeAdminDto();
        dto.setId(like.getId());
        dto.setMarkId(like.getMarkId());
        dto.setUserId(like.getUserId());
        dto.setAuthor(user != null ? user.getFullName() : "User");
        dto.setPhone(user != null ? user.getPhone() : "—");
        dto.setCreatedAt(like.getCreatedAt());
        dto.setCreatedAtText(formatInstant(like.getCreatedAt()));
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
