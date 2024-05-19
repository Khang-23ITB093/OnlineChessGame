package org.example.onlinechessgame.model.server;

import org.example.onlinechessgame.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Server server;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private ClientHandler opponent;

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());



            while (true) {
                Message message = (Message) ois.readObject();
                switch (message.getType()) {
                    case MOVE -> {
                        System.out.println("Received move from client!" + message.getData().toString());
                        // Truyền object Board đến đối thủ
                        opponent.sendMessage(message);
                    }

                    case MATCH ->  {
                        server.addWaitingPlayer(this);
                        System.out.println("Player added to waiting list");
                    }

                    case PROMOTE -> {
                        System.out.println("Received promote from client!" + message.getData().toString());
                        // Truyền object Board đến đối thủ
                        opponent.sendMessage(message);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientHandler getOpponent() {
        return opponent;
    }

    public void setOpponent(ClientHandler opponent) {
        this.opponent = opponent;
    }
}
