package com.chat.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 20000; // Port number for the server
    private static List<PrintWriter> clients = new ArrayList<>(); // List to store client output streams

    public static void main(String[] args) {
        System.out.println("Starting chat server...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                // Wait for a new client to connect
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Create output stream for the client
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.add(out);

                // Start a new thread for each client
                Thread clientThread = new Thread(new ClientHandler(clientSocket, out));
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    // Client handler class to manage each client
    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket, PrintWriter out) {
            this.socket = socket;
            this.out = out;
        }

        @Override
        public void run() {
            try {
                // Input stream to read messages from the client
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;

                // Read messages from the client
                while ((message = in.readLine()) != null) {
                    System.out.println("Received message: " + message);
                    broadcast(message); // Broadcast the message to all clients
                }
            } catch (IOException e) {
                System.out.println("Client error: " + e.getMessage());
            } finally {
                if (out != null) {
                    clients.remove(out); // Remove client from the list if disconnected
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Broadcast message to all clients
        private void broadcast(String message) {
            for (PrintWriter client : clients) {
                client.println(message);
            }
        }
    }
}