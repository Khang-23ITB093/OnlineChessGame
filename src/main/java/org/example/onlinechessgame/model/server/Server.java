package org.example.onlinechessgame.model.server;

import org.example.onlinechessgame.model.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Server {
    private static final int PORT = 12345;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private Queue<ClientHandler> waitingPlayers = new LinkedList<>();

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected" + socket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(this, socket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addWaitingPlayer(ClientHandler clientHandler) {
        waitingPlayers.add(clientHandler);
        if (waitingPlayers.size() >= 2) {
            System.out.println("Match found!");
            ClientHandler player1 = waitingPlayers.poll();
            ClientHandler player2 = waitingPlayers.poll();
            player1.setOpponent(player2);
            player2.setOpponent(player1);

            System.out.println("Dang gui");
            player1.sendMessage(new Message(Message.MessageType.MATCH, "WHITE"));
            player2.sendMessage(new Message(Message.MessageType.MATCH, "BLACK"));;
        }
    }

    public synchronized void broadcast(ClientHandler sender, Message message) {
        sender.sendMessage(message);
    }
}
