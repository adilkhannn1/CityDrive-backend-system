package kz.citydrive.admin.dto;

public class DocumentCreateRequest {

    private String titleRu;
    private String titleKk;
    private String titleEn;
    private String url;
    private int sortOrder;
    private boolean active = true;

    public String getTitleRu() {
        return titleRu;
    }

    public void setTitleRu(String titleRu) {
        this.titleRu = titleRu;
    }

    public String getTitleKk() {
        return titleKk;
    }

    public void setTitleKk(String titleKk) {
        this.titleKk = titleKk;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
