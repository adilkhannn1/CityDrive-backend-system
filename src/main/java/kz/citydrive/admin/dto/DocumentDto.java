package kz.citydrive.admin.dto;

import kz.citydrive.admin.domain.LegalDocument;

import java.time.Instant;

public class DocumentDto {

    private Long id;
    private String title;
    private String content;
    private String url;
    private String type;
    private Instant createdAt;
    private Instant updatedAt;

    public static DocumentDto fromEntity(LegalDocument document, String lang) {
        DocumentDto dto = new DocumentDto();
        dto.setId(document.getId());
        dto.setTitle(resolveTitle(document, lang));
        dto.setContent(resolveContent(document, lang));
        dto.setType(document.getType());
        if ("url".equals(document.getType())) {
            dto.setUrl(dto.getContent());
        }
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        return dto;
    }

    private static String resolveTitle(LegalDocument document, String lang) {
        return switch (normalizeLang(lang)) {
            case "kk" -> document.getTitleKk();
            case "en" -> document.getTitleEn();
            default -> document.getTitleRu();
        };
    }

    private static String resolveContent(LegalDocument document, String lang) {
        return switch (normalizeLang(lang)) {
            case "kk" -> document.getContentKk();
            case "en" -> document.getContentEn();
            default -> document.getContentRu();
        };
    }

    private static String normalizeLang(String lang) {
        if (lang == null || lang.isBlank()) {
            return "ru";
        }
        String value = lang.toLowerCase();
        if (value.startsWith("kk")) {
            return "kk";
        }
        if (value.startsWith("en")) {
            return "en";
        }
        return "ru";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
