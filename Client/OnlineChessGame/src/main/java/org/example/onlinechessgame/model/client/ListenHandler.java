package org.example.onlinechessgame.model.client;

import javafx.application.Platform;
import org.example.onlinechessgame.Tile;
import org.example.onlinechessgame.controllers.LoginController;
import org.example.onlinechessgame.model.History;
import org.example.onlinechessgame.model.Message;
import org.example.onlinechessgame.model.Move;
import org.example.onlinechessgame.model.User;
import org.example.onlinechessgame.pieces.Piece;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

public class ListenHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream ois;
    private Object data;
    private LoginController loginController;
    private Client client;
    private boolean running = true;

    public ListenHandler(Socket socket, Client client, LoginController loginController) {
        this.socket = socket;
        this.client = client;
        this.loginController = loginController;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (running) {
            try {
                Message message = (Message) ois.readObject();
                System.out.println("Received message from Server!" + message.getType().toString());
                switch (message.getType()) {
                    case Message.MessageType.MOVE:
                        System.out.println("Received move from opponent!");
                        data = message.getData();
                        if (data instanceof Move)
                            client.getController().opponentMovePiece((Move) data);
                        else System.out.println("Invalid data type");
                        break;

                    case Message.MessageType.DISCONNECT:
                        socket.close();
                        break;

                    case Message.MessageType.MATCH:
                        Platform.runLater(() -> {
                            client.getHomeController().matchSuccess(message.getData().toString());
                            client.getHomeController().hide();
                        });
                        if (client.getController() == null) {
                            client.setController(client.getHomeController().getController());
                            Platform.runLater(() -> {
                                client.getHomeController().hide();
                            });
                        }
                        // Start game & set color
                        System.out.println("Match found! You play as " + message.getData().toString());
                        break;

                    case REMATCH:
                        System.out.println("Received rematch request from opponent! " + message.getData().toString());
                        Platform.runLater(() -> {
                            client.getController().newGame(message.getData().toString().equals("WHITE"));
                        });
                        break;

                    case Message.MessageType.PROMOTE:
                        System.out.println("Received promote from opponent!");
                        data = message.getData();
                        if (data instanceof Tile) {
                            Platform.runLater(() -> {
                                client.getController().getBoard().promotePawn((Tile) data, ((Tile) data).getPiece().getType());
                                client.getController().getBoard().setPiece(((Tile) data).getPiece(), ((Tile) data).getRow(), ((Tile) data).getCol());
                            });
                        } else System.out.println("Invalid data type");
                        break;

                    case LOGIN:
                        System.out.println("Login successful!");
                        Platform.runLater(() -> loginController.loginSuccess());
                        break;

                    case LOGIN_FAILED:
                        System.out.println("Login failed!");
                        loginController.loginFailed();
                        break;

                    case CHECK_USERNAME:
                        if ((boolean) message.getData())
                            loginController.usernameExists();
                        break;

                    case CHECK_EMAIL:
                        if ((boolean) message.getData())
                            loginController.emailExists();
                        break;

                    case QUICK_LOGIN:
                        System.out.println("Quick login successful!");
                        Platform.runLater(() -> loginController.loginSuccess());
                        break;

                    case QUICK_LOGIN_FAILED:
                        System.out.println("Quick login failed!");
                        Platform.runLater(() -> loginController.loginFailed());
                        break;
                    case REGISTER:
                        System.out.println("Register successful!");
                        Platform.runLater(() -> loginController.registerSuccess());
                        break;

                    case INFORMATION_MATCH:
                        System.out.println("Information match!");
                        User[] users = (User[]) message.getData();
                        Platform.runLater(() -> client.getController().setInformationPlayers(users[0], users[1]));
                        break;

                    case LOSE:
                        System.out.println("You lose!");
                        Platform.runLater(() -> client.getController().losing());
                        break;

                    case WIN:
                        System.out.println("You win!");
                        Platform.runLater(() -> client.getController().winning());
                        break;

                    case MESSAGE:
                        System.out.println("Received message from opponent!");
                        Platform.runLater(() -> client.getController().displayMessage(message.getData().toString()));

                    case GET_DATA:
                        System.out.println("Received data from server!");
                        data = message.getData();
                        if (data instanceof User)
                            Platform.runLater(() -> client.getHomeController().setUser((User) data));
                        else System.out.println("Invalid data type");
                        break;

                    case GET_HISTORY:
                        System.out.println("Received history from server!");
                        Platform.runLater(() -> {
                            client.getHomeController().setHistoryList((List<History>) message.getData());
                        });
                        break;

                    case REQUEST_DRAW:
                        System.out.println("Opponent request draw!");
                        Platform.runLater(() -> {
                            try {
                                client.getController().draw();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        break;

                    case DRAW:
                    case ACCEPT_DRAW:
                        System.out.println("Draw!");
                        Platform.runLater(() -> {
                            client.getController().showDraw();
                        });
                        break;

                    default:
                        System.out.println("Invalid message type" + message.getType() + message.getData().toString());

                }
            } catch (IOException e) {
                running = false;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
