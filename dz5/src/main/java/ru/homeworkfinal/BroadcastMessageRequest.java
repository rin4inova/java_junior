package ru.homeworkfinal;

public class BroadcastMessageRequest {
    public static final String TYPE = "broadcast";


    private String message;
    private String sender;

    public String getType() {
        return TYPE;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
