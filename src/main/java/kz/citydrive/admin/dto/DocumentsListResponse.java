package kz.citydrive.admin.dto;

import java.util.List;

public class DocumentsListResponse {

    private List<DocumentDto> data;

    public DocumentsListResponse(List<DocumentDto> data) {
        this.data = data;
    }

    public List<DocumentDto> getData() {
        return data;
    }

    public void setData(List<DocumentDto> data) {
        this.data = data;
    }
}
