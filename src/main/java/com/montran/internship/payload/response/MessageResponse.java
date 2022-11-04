package com.montran.internship.payload.response;

public class MessageResponse {
    private String message;
    private String status;

    public MessageResponse(String message) {
        this.message = message;
    }

    public MessageResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
