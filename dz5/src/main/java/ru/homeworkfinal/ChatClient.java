package ru.homeworkfinal;

import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private final static ObjectMapper smileObjectMapper = new ObjectMapper(new SmileFactory());
    private final static ObjectMapper textObjectMapper = new ObjectMapper(); // Новый ObjectMapper для текстовых данных

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        System.out.print("Введите ваш логин: ");
        String clientLogin = console.nextLine();

        try (Socket server = new Socket("localhost", 8888)) {
            System.out.println("Успешно подключились к серверу");

            try (OutputStream out = server.getOutputStream();
                 InputStream in = server.getInputStream()) {

                // Создаем и отправляем запрос на вход
                byte[] loginRequest = createLoginRequest(clientLogin);
                out.write(loginRequest);
                out.flush();

                // Читаем ответ от сервера
                byte[] responseBuffer = new byte[1024];
                int bytesRead = in.read(responseBuffer);
                if (bytesRead == -1) {
                    System.out.println("Сервер закрыл соединение.");
                    return;
                }
                // Используем текстовый ObjectMapper для обработки ответа
                LoginResponse loginResponse = smileObjectMapper.readValue(responseBuffer, 0, bytesRead, LoginResponse.class);

                if (!loginResponse.isConnected()) {
                    System.out.println("Не удалось подключиться к серверу");
                    return;
                }

                new Thread(() -> {
                    try {
                        byte[] msgBuffer = new byte[1024];
                        while (true) {
                            int msgBytesRead = in.read(msgBuffer);
                            if (msgBytesRead == -1) break;
                            String msgFromServer = new String(msgBuffer, 0, msgBytesRead);
                            System.out.println("Сообщение от сервера: " + msgFromServer);
                        }
                    } catch (IOException e) {
                        System.err.println("Ошибка чтения сообщения от сервера: " + e.getMessage());
                    }
                }).start();

                while (true) {
                    System.out.println("Введите сообщение для всех (или 'exit' для выхода):");
                    String message = console.nextLine();
                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                    BroadcastMessageRequest request = new BroadcastMessageRequest();
                    request.setMessage(message); // Используем введенное сообщение
                    request.setSender(clientLogin); // Указываем имя пользователя
                    byte[] messageBytes = smileObjectMapper.writeValueAsBytes(request); // Используем smileObjectMapper
                    out.write(messageBytes);
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка во время подключения к серверу: " + e.getMessage());
        }

        System.out.println("Отключились от сервера");
    }

    private static byte[] createLoginRequest(String login) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(login);
        try {
            return smileObjectMapper.writeValueAsBytes(loginRequest);
        } catch (JsonProcessingException e) {
            System.err.println("Ошибка создания запроса на вход: " + e.getMessage());
            return null;
        }
    }
}