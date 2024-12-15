package ru.homeworkfinal;

public class LoginResponse {
    private boolean connected;
    private String message;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getMessage() {
        return message; // Геттер для сообщения
    }

    public void setMessage(String message) {
        this.message = message; // Сеттер для сообщения
    }
}