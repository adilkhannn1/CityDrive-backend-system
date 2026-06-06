package kz.citydrive.admin.dto;

public class CityCreateRequest {

    private String name;
    private int sortOrder = 1;
    private boolean active = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
