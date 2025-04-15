package com.chat.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "192.168.0.100"; // Server IP or localhost
    private static final int SERVER_PORT = 20000; // Server port

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.println("Connected to server: " + SERVER_ADDRESS);
            System.out.println("Type your message (type 'exit' to quit):");

            // Thread to receive messages from the server
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println("New message: " + message);
                    }
                } catch (IOException e) {
                    System.out.println("Error receiving message: " + e.getMessage());
                }
            });
            receiveThread.start();

            // Send messages to the server
            Scanner scanner = new Scanner(System.in);
            String message;
            while (true) {
                message = scanner.nextLine();
                if ("exit".equalsIgnoreCase(message)) {
                    break; // Exit the program
                }
                out.println(message); // Send message to the server
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}