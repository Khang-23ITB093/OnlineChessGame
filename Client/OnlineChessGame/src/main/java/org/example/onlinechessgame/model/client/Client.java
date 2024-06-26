package org.example.onlinechessgame.model.client;

import org.example.onlinechessgame.ChessApp;
import org.example.onlinechessgame.Tile;
import org.example.onlinechessgame.controllers.ChessBoardController;
import org.example.onlinechessgame.controllers.HomeController;
import org.example.onlinechessgame.controllers.LoginController;
import org.example.onlinechessgame.model.Message;
import org.example.onlinechessgame.model.Move;
import org.example.onlinechessgame.util.QuickLoginUtil;

import javax.net.ssl.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;

public class Client {
    private String serverAddress;
    private int serverPort;
    private SSLSocket socket; // Sử dụng SSLSocket
    private SSLContext sslContext;
    private ObjectOutputStream oos;
    private ChessBoardController controller;
    private LoginController loginController;
    private HomeController homeController;


    //Xử lý kết nối với server với đăng nhập, đăng ký, quên mật khẩu
    public Client(String serverAddress, int serverPort, LoginController loginController) {
        this.serverAddress = serverAddress;
//        this.serverAddress = "192.168.1.130";
        this.serverPort = serverPort;
        this.loginController = loginController;
    }

    public void requestLogin(String[] strings) throws IOException {
        oos.writeObject(new Message(Message.MessageType.LOGIN, strings));
    }

    public void requestRegister(String[] strings) throws IOException {
        oos.writeObject(new Message(Message.MessageType.REGISTER, strings));
    }


    //Xử lý kết nối với server với trò chơi
    public void setController(ChessBoardController controller) {
        this.controller = controller;
    }

    public void connectToServer() throws Exception {
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, getTrustManagers(), null);

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        socket = (SSLSocket) sslSocketFactory.createSocket(serverAddress, serverPort);

        oos = new ObjectOutputStream(socket.getOutputStream());
        new Thread(new ListenHandler(socket, this, loginController)).start();
    }

    private TrustManager[] getTrustManagers() throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (FileInputStream trustStoreFis = new FileInputStream(Paths.get(ChessApp.class.getResource("/org/example/onlinechessgame/Certificate/client.truststore").toURI()).toString())) {
            trustStore.load(trustStoreFis, "chessgame".toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        return trustManagerFactory.getTrustManagers();
    }


    public void requestMatchmaking() throws IOException {
        // ... gửi yêu cầu ghép đấu đến server ...
        oos.writeObject(new Message(Message.MessageType.MATCH, null));
    }

    public void requestCancelMatchmaking() throws IOException {
        // ... gửi yêu cầu hủy ghép đấu đến server ...
        oos.writeObject(new Message(Message.MessageType.CANCEL, null));
    }

    public void requestRematch() throws IOException {
        // ... gửi yêu cầu tái đấu đến server ...
        oos.writeObject(new Message(Message.MessageType.REMATCH, null));
    }

    public void requestCancelRematch() throws IOException {
        // ... gửi yêu cầu hủy tái đấu đến server ...
        oos.writeObject(new Message(Message.MessageType.CANCEL_REMATCH, null));
    }

    public void requestQuickLogin() throws Exception {
        if (!Files.exists(Paths.get(QuickLoginUtil.filepath))) {
            loginController.loginFailed();
            return;
        }
        oos.writeObject(new Message(Message.MessageType.QUICK_LOGIN, QuickLoginUtil.readFile()));
    }

    public void requestDraw() throws IOException {
        oos.writeObject(new Message(Message.MessageType.REQUEST_DRAW, null));
    }

    public void requestAcceptDraw() throws IOException {
        oos.writeObject(new Message(Message.MessageType.ACCEPT_DRAW, null));
    }

    public void getData() throws IOException {
        oos.writeObject(new Message(Message.MessageType.GET_DATA, null));
    }

    public void winGame() throws IOException {
        // ... gửi thông báo thắng đến server ...
        oos.writeObject(new Message(Message.MessageType.WIN, null));
    }

    public void loseGame() throws IOException {
        // ... gửi thông báo thua đến server ...
        oos.writeObject(new Message(Message.MessageType.LOSE, null));
    }

    public void drawGame() throws IOException {
        // ... gửi thông báo hòa đến server ...
        oos.writeObject(new Message(Message.MessageType.DRAW, null));
    }

    public void requestOut() throws IOException {
        // ... gửi thông báo out không rematch ...
        oos.writeObject(new Message(Message.MessageType.OUT, null));
    }

    public void requestExit() throws IOException {
        // ... gửi thông báo exit thoát chương trình ...
        oos.writeObject(new Message(Message.MessageType.EXIT, null));
    }

    public void requestGetHistory() throws IOException {
        // ... gửi thông báo lấy lịch sử đấu ...
        oos.writeObject(new Message(Message.MessageType.GET_HISTORY, null));
    }

    public void sendChat(String message) throws IOException {
        oos.writeObject(new Message(Message.MessageType.MESSAGE, message));
    }

    public void movePiece(Move move) throws IOException {
        // ... gửi nước đi của mình đến opponent ...
        oos.writeObject(new Message(Message.MessageType.MOVE, move));
    }

    public void promotePiece(Tile pawnTile) throws IOException {
        // ... gửi nước đi của mình đến opponent ...
        oos.writeObject(new Message(Message.MessageType.PROMOTE, pawnTile));
    }

    public void disconnectFromServer() throws IOException {
        // ... gửi thông báo ngắt kết nối đến server ...
        oos.writeObject(new Message(Message.MessageType.DISCONNECT, null));
        socket.close();
    }


    public ChessBoardController getController() {
        return controller;
    }

    public HomeController getHomeController() {
        return homeController;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
}
