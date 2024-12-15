package ru.homeworkfinal;
public class SendMessageRequest {
    public static final String TYPE = "sendMessage";

    private String type;
    private String recipient;
    private String message;

    public SendMessageRequest() {
        setType(TYPE);
    }

    public String getType() {
        return type; // Метод для получения типа
    }

    public void setType(String type) {
        this.type = type; // Метод для установки типа
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}