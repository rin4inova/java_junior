package ru.homeworkfinal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private final static ObjectMapper objectMapper = new ObjectMapper(new SmileFactory());

    public static void main(String[] args) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
        try (ServerSocket server = new ServerSocket(8888)) {
            System.out.println("Сервер запущен");

            while (true) {
                System.out.println("Ждем клиентского подключения");
                Socket client = server.accept();
                ClientHandler clientHandler = new ClientHandler(client, clients);
                new Thread(clientHandler).start(); // Запускаем новый поток для каждого клиента
            }
        } catch (IOException e) {
            System.err.println("Ошибка во время работы сервера: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {

        private final Socket client;
        private final Map<String, ClientHandler> clients;
        private String clientLogin;

        public ClientHandler(Socket client, Map<String, ClientHandler> clients) {
            this.client = client;
            this.clients = clients;
        }

        public void run() {
            try (InputStream in = client.getInputStream();
                 OutputStream out = client.getOutputStream()) {

                // Чтение логина
                byte[] loginRequestBuffer = new byte[1024];
                int bytesRead = in.read(loginRequestBuffer);
                LoginRequest request = objectMapper.readValue(loginRequestBuffer, 0, bytesRead, LoginRequest.class);
                this.clientLogin = request.getLogin();

                if (clients.containsKey(clientLogin)) {
                    sendMessage(createLoginResponse(false)); // Отправляем ответ в формате Smile
                    return;
                }

                clients.put(clientLogin, this);
                sendMessage(createLoginResponse(true)); // Отправляем успешный ответ в формате Smile

                // Чтение сообщений от клиента
                while (true) {
                    byte[] msgBuffer = new byte[1024];
                    bytesRead = in.read(msgBuffer);
                    if (bytesRead == -1) break; // Если клиент отключился

                    handleClientMessage(msgBuffer, bytesRead);
                }
            } catch (IOException e) {
                System.err.println("Ошибка в обработчике клиента: " + e.getMessage());
            } finally {
                doClose();
            }
        }

        private void handleClientMessage(byte[] msgFromClient, int bytesRead) {
            try {
                BroadcastMessageRequest broadcastRequest = objectMapper.readValue(msgFromClient, 0, bytesRead, BroadcastMessageRequest.class);
                broadcastMessage(broadcastRequest.getMessage(), broadcastRequest.getSender());
            } catch (IOException e) {
                System.err.println("Ошибка обработки сообщения от клиента [" + clientLogin + "]: " + e.getMessage());
            }
        }

        private void broadcastMessage(String message, String sender) {
            for (ClientHandler clientHandler : clients.values()) {
                if (!clientHandler.clientLogin.equals(sender)) { // Не отправляем сообщение отправителю
                    // Создаем Login Response с сообщением
                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.setMessage(sender + ": " + message); // Добавляем имя отправителя
                    // Сериализуем в формат Smile
                    byte[] messageBytes;
                    try {
                        messageBytes = objectMapper.writeValueAsBytes(loginResponse);
                        clientHandler.sendMessage(messageBytes); // Передаем массив байтов
                    } catch (JsonProcessingException e) {
                        System.err.println("Ошибка сериализации сообщения: " + e.getMessage());
                    }
                }
            }
        }

        private void doClose () {
            try {
                client.close();
                clients.remove(clientLogin);
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }

        public void sendMessage(byte[] messageBytes) {
            try {
                OutputStream out = client.getOutputStream();
                out.write(messageBytes);
                out.flush();
            } catch (IOException e) {
                System.err.println("Ошибка отправки сообщения клиенту: " + e.getMessage());
            }
        }

        private byte[] createLoginResponse(boolean success) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setConnected(success);
            try {
                return objectMapper.writeValueAsBytes(loginResponse); // Сериализуем в байты
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Не удалось создать loginResponse: " + e.getMessage());
            }
        }
    }
}