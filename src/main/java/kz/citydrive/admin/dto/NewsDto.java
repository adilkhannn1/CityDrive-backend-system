package kz.citydrive.admin.dto;

import kz.citydrive.admin.domain.News;
import java.time.Instant;

public class NewsDto {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Instant publishedAt;
    private boolean isPublished;

    public static NewsDto fromEntity(News news) {
        NewsDto dto = new NewsDto();
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setDescription(news.getDescription());
        dto.setImageUrl(news.getImageUrl());
        dto.setPublishedAt(news.getPublishedAt());
        dto.setPublished(news.isPublished());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }
}
