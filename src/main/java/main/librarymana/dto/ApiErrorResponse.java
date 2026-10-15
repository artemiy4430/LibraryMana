package main.librarymana.dto;

import java.time.Instant;

public class ApiErrorResponse {
    private Instant timestamp;
    private int status;
    private String code;
    private String message;


    public ApiErrorResponse(Instant timestamp, int status,
                            String code, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
