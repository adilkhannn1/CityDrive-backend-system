package kz.citydrive.admin.dto;

import java.util.Map;

public class DeleteResponse {

    private boolean success = true;
    private String message;

    public DeleteResponse(String message) {
        this.message = message;
    }

    public static Map<String, Object> ok(String message) {
        return Map.of("success", true, "message", message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
