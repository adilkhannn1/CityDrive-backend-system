package kz.citydrive.admin.dto;

public class ApiMessageResponse {

    private String message;
    private Integer statusCode;

    public ApiMessageResponse(String message) {
        this.message = message;
    }

    public ApiMessageResponse(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
