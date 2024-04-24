package BT2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client2 {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 2005);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter your name: ");
        String name = consoleReader.readLine();
        writer.writeBytes(name + "\n"); // Gửi tên của client đến server

        Thread receiveThread = new Thread(() -> {
            try {
                while (true) {
                    String message = reader.readLine();
                    if (message == null) {
                        break;
                    }
                    System.out.println(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        receiveThread.start();

        while (true) {
            String message = consoleReader.readLine();
            writer.writeBytes(name + ": " + message + "\n"); // Thêm tên vào tin nhắn
            if (message.equalsIgnoreCase("bye")) {
                break;
            }
        }
        receiveThread.join();
        socket.close();
    }
}
