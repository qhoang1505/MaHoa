package BT2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ArrayList<ClientHandler> clientHandlers;

    public Server() throws Exception {
        clientHandlers = new ArrayList<>();
        ServerSocket serverSocket = new ServerSocket(2005);
        System.out.println("Server is running on port 1511");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New client connected: " + socket);

            ClientHandler clientHandler = new ClientHandler(socket);
            clientHandlers.add(clientHandler);
            clientHandler.start();
        }
    }

    public static void main(String[] args) throws Exception {
        new Server();
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private DataOutputStream writer;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new DataOutputStream(socket.getOutputStream());
                // Yêu cầu client gửi tên của họ đến server
                writer.writeBytes("Enter your name: \n");
                clientName = reader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                while (true) {
                    String message = reader.readLine();
                    if (message == null || message.equalsIgnoreCase("bye")) {
                        break;
                    }
                    System.out.println(clientName + ": " + message); // Hiển thị tên của người gửi
                    broadcast(clientName + ": " + message); // Phát tin nhắn cho tất cả client
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    clientHandlers.remove(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(String message) {
            for (ClientHandler handler : clientHandlers) {
                try {
                    if (handler != this) {
                        handler.writer.writeBytes(message + "\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
