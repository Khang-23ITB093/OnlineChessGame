package org.example.onlinechessgame.model.client;

import javafx.application.Platform;
import org.example.onlinechessgame.Tile;
import org.example.onlinechessgame.controllers.ChessBoardController;
import org.example.onlinechessgame.model.Message;
import org.example.onlinechessgame.model.Move;
import org.example.onlinechessgame.pieces.PieceType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ListenHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream ois;
    private Object data;
    private ChessBoardController controller;

    public ListenHandler(Socket socket, ChessBoardController controller) {
        this.socket = socket;
        this.controller = controller;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
        @Override
        public void run() {
            while (true){
                try {
                    Message message = (Message) ois.readObject();
                    System.out.println("Received message from Server!"+ message.getType().toString());
                    switch (message.getType()){
                        case Message.MessageType.MOVE:
                            System.out.println("Received move from opponent!");
                            data = message.getData();
                            if (data instanceof Move)
                                controller.opponentMovePiece((Move) data);
                            else System.out.println("Invalid data type");
                            break;

                        case Message.MessageType.DISCONNECT:
                            socket.close();
                            break;

                        case Message.MessageType.ERROR:
                            System.out.println("Error: " + message.getData());
                            break;

                        case Message.MessageType.MATCH:
                            // Start game & set color
                            System.out.println("Match found! You play as " + message.getData().toString());
                            controller.startGame(message.getData().equals("WHITE"));
                            break;

                        case Message.MessageType.PROMOTE:
                            System.out.println("Received promote from opponent!");
                            data = message.getData();
                            if (data instanceof Tile) {
                                Platform.runLater(() ->{
                                    controller.getBoard().promotePawn((Tile)data, ((Tile) data).getPiece().getType());
                                    controller.getBoard().setPiece(((Tile) data).getPiece(), ((Tile) data).getRow(), ((Tile) data).getCol());
                                });
                            }
                            else System.out.println("Invalid data type");
                            break;
                        default:
                            System.out.println("Invalid message type" + message.getType() + message.getData().toString());

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
}
