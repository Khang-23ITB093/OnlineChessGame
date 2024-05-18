package org.example.onlinechessgame.model.client;

import org.example.onlinechessgame.Board;
import org.example.onlinechessgame.controllers.ChessBoardController;
import org.example.onlinechessgame.model.Message;
import org.example.onlinechessgame.model.Move;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private ObjectOutputStream oos;
    private ChessBoardController controller;

    public Client(String serverAddress, int serverPort, ChessBoardController controller) {
        this.controller = controller;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void connectToServer() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        //  tạo luồng gửi/nhận dữ liệu với server ...
        oos = new ObjectOutputStream(socket.getOutputStream());
        new Thread(new ListenHandler(socket, controller)).start();
    }

    public void requestMatchmaking() throws IOException {
        // ... gửi yêu cầu ghép đấu đến server ...
        oos.writeObject(new Message(Message.MessageType.MATCH, null));
    }

    public void movePiece(Move move) throws IOException {
        // ... gửi nước đi của mình đến opponent ...
        oos.writeObject(new Message(Message.MessageType.MOVE, move));
    }

    public void disconnectFromServer() throws IOException {
        // ... gửi thông báo ngắt kết nối đến server ...
        oos.writeObject(new Message(Message.MessageType.DISCONNECT, null));
        socket.close();
    }

}
