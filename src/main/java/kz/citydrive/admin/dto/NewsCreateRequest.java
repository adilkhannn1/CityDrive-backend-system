package kz.citydrive.admin.dto;

public class NewsCreateRequest {

    private String title;
    private String description;
    private String content;
    private String imageUrl;
    // ISO-8601 string, e.g. "2026-05-29T10:00:00Z"; null → Instant.now()
    private String publishedAt;
    private boolean published = true;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}
