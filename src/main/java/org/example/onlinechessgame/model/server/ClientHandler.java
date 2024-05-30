package org.example.onlinechessgame.model.server;

import org.example.onlinechessgame.model.Message;
import org.example.onlinechessgame.model.User;
import org.example.onlinechessgame.util.QuickLoginUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Base64;

import static org.example.onlinechessgame.util.QuickLoginUtil.ALGORITHM;

public class ClientHandler implements Runnable{
    private Server server;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private ClientHandler opponent;
    private User user;
    private User opponentUser;
    private boolean running = true;


    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());



            while (running) {
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

                    case CANCEL -> {
                        System.out.println("Received cancel from client!");
                        server.cancelWaiting(this);
                    }

                    case PROMOTE -> {
                        System.out.println("Received promote from client!" + message.getData().toString());
                        // Truyền object Board đến đối thủ
                        opponent.sendMessage(message);
                    }

                    case DISCONNECT -> {
                        server.removeClient(this);
                        socket.close();
                        System.out.println("Client disconnected!");
                    }

                    case LOGIN -> {
                        String[] encryptedData = (String[]) message.getData();
                        String encryptedUsername = encryptedData[0];
                        String encryptedPassword = encryptedData[1];
                        String encodedKey = encryptedData[2];

                        // Giải mã
                        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
                        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);

                        String username = QuickLoginUtil.decrypt(encryptedUsername, secretKey);
                        String password = QuickLoginUtil.decrypt(encryptedPassword, secretKey);

                        System.out.println("Received login request!" + username + " " + password);
                        if (server.getServerDatabaseHandler().authenticateUser(username, password)) {
                            oos.writeObject(new Message(Message.MessageType.LOGIN, null));
                            // Lấy thông tin user từ database
                            user = server.getServerDatabaseHandler().getUser(username);
                        } else {
                            oos.writeObject(new Message(Message.MessageType.LOGIN_FAILED, null));
                        }
                    }

                    case QUICK_LOGIN -> {
                        String[] encryptedData = (String[]) message.getData();
                        String encryptedUsername = encryptedData[0];
                        String encryptedPassword = encryptedData[1];
                        String encodedKey = encryptedData[2];

                        // Giải mã
                        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
                        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);

                        String username = QuickLoginUtil.decrypt(encryptedUsername, secretKey);
                        String password = QuickLoginUtil.decrypt(encryptedPassword, secretKey);

                        // Xác thực
                        if (server.getServerDatabaseHandler().authenticateUser(username, password)) {
                            oos.writeObject(new Message(Message.MessageType.QUICK_LOGIN, null));
                            // Lấy thông tin user từ database
                            user = server.getServerDatabaseHandler().getUser(username);
                        } else {
                            oos.writeObject(new Message(Message.MessageType.QUICK_LOGIN_FAILED, null));
                        }
                    }


                    case REGISTER -> {
                        String[] data = (String[]) message.getData();
                        System.out.println("Received register request!" + data[2] + " " + data[1] + " " + data[2]);
                        if (server.getServerDatabaseHandler().checkEmail(data[0])) {
                            oos.writeObject(new Message(Message.MessageType.CHECK_EMAIL, true));
                        } else if (server.getServerDatabaseHandler().checkUsername(data[2])) {
                            oos.writeObject(new Message(Message.MessageType.CHECK_USERNAME, true));
                        }else {
                            server.getServerDatabaseHandler().registerUser(data);
                            oos.writeObject(new Message(Message.MessageType.REGISTER, null));
                        }
                    }

                    case WIN -> {
                        System.out.println("Received win from winner(email): " + user.getEmail());
                        System.out.println("Update points for winner: " + user.getUsername());
                        server.getServerDatabaseHandler().updatePlayerPoints(true, user);
                        if (opponent != null) {
                            opponent.sendMessage(new Message(Message.MessageType.LOSE, null));
                            System.out.println("Update points for loser: " + opponent.getUser().getUsername());
                            opponent.getServer().getServerDatabaseHandler().updatePlayerPoints(false, opponent.getUser());
                        }
                        else {
                            System.out.println("Update points for loser: " + opponentUser.getUsername());
                            getServer().getServerDatabaseHandler().updatePlayerPoints(false, opponentUser);
                        }
                    }

                    case LOSE -> {
                        System.out.println("Received lose from loser(email): " + user.getEmail());
                        System.out.println("Update points for winner: " + user.getUsername());
                        server.getServerDatabaseHandler().updatePlayerPoints(false, user);
                        if (opponent != null) {
                            opponent.sendMessage(new Message(Message.MessageType.WIN, null));
                            System.out.println("Update points for Winner: " + opponent.getUser().getUsername());
                            opponent.getServer().getServerDatabaseHandler().updatePlayerPoints(true, opponent.getUser());
                        }
                        else {
                            System.out.println("Update points for Winner: " + opponentUser.getUsername());
                            getServer().getServerDatabaseHandler().updatePlayerPoints(true, opponentUser);
                        }
                    }

                    case REMATCH -> {
                        server.requestRematch(this);
                    }

                    case CANCEL_REMATCH -> {
                        server.cancelRematch(this);
                    }

                    case EXIT -> {
                        System.out.println("Client " + user.getUsername() + " is exit!");
                        running = false;
                        if (opponent != null) {
                            opponent.sendMessage(new Message(Message.MessageType.WIN, null));
                            opponent.setOpponent(null);
                        }
                        socket.close();
                    }
                    // Thông báo out không rematch (user)
                    case OUT -> {
                        System.out.println("Client out. Not rematch!");
                        opponent = null;
                    }

                    case MESSAGE -> {
                        opponent.sendMessage(message);
                    }

                    case GET_DATA -> {
                        oos.writeObject(new Message(Message.MessageType.GET_DATA, user));
                    }

                    case DRAW -> {
                        System.out.println("Received draw from client!");
                        // Truyền object Board đến đối thủ
                        opponent.sendMessage(message);
                    }

                    case ACCEPT_DRAW -> {
                        System.out.println("Received accept draw from client!");
                        // Truyền object Board đến đối thủ
                        opponent.sendMessage(message);
                    }

                }
            }
        } catch (SocketException socketException){
            System.out.println("Client exit!");
            running = false;
            if (opponent != null) {
                opponent.sendMessage(new Message(Message.MessageType.WIN, null));
                opponent.setOpponent(null);
            }
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    public User getUser() {
        return user;
    }

    public Server getServer() {
        return server;
    }

    public void setOpponentUser(User opponentUser) {
        this.opponentUser = opponentUser;
    }
}
