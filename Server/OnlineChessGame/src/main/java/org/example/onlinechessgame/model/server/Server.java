package org.example.onlinechessgame.model.server;

import org.example.onlinechessgame.Board;
import org.example.onlinechessgame.model.Message;
import org.example.onlinechessgame.model.User;
import org.example.onlinechessgame.util.ServerDatabaseHandler;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.Socket;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Server {
    private static final int PORT = 12345;
    private SSLServerSocket serverSocket; // Sử dụng SSLServerSocket
    private List<ClientHandler> clients = new ArrayList<>();
    private Queue<ClientHandler> waitingPlayers = new LinkedList<>();
    private ServerDatabaseHandler serverDatabaseHandler = new ServerDatabaseHandler();
    private List<ClientHandler> rematchRequests = new ArrayList<>();
    private SSLContext sslContext;
    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(PORT);

            System.out.println("Secure server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(this, socket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private KeyManager[] getKeyManagers() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream keyStoreFis = new FileInputStream(Paths.get(getClass().getResource("/org/example/onlinechessgame/Certificate/server.keystore").toURI()).toString())) {
            keyStore.load(keyStoreFis, "chessgame".toCharArray()); // Mật khẩu của keystore
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, "chessgame".toCharArray());

        return keyManagerFactory.getKeyManagers();
    }

//    // Tạo TrustManager để chấp nhận chứng chỉ client (optional, cho xác thực hai chiều)
//    private TrustManager[] getTrustManagers() throws Exception {
//        KeyStore trustStore = KeyStore.getInstance("JKS");
//        try (FileInputStream trustStoreFis = new FileInputStream("server.truststore")) {
//            trustStore.load(trustStoreFis, "password".toCharArray());
//        }
//
//        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        trustManagerFactory.init(trustStore);
//
//        return trustManagerFactory.getTrustManagers();
//    }

    public synchronized void addWaitingPlayer(ClientHandler clientHandler) throws InterruptedException {
        waitingPlayers.add(clientHandler);
        if (waitingPlayers.size() >= 2) {
            System.out.println("Match found!");
            ClientHandler player1 = waitingPlayers.poll();
            ClientHandler player2 = waitingPlayers.poll();
            User[] users1 = {player1.getUser(), player2.getUser()};
            User[] users2 = {player2.getUser(), player1.getUser()};
            startMatch(player1, player2, Message.MessageType.MATCH);
        }
    }

    public synchronized void requestRematch(ClientHandler clientHandler) throws InterruptedException {
        System.out.println("Received rematch request from " + clientHandler.getUser().getUsername());
        ClientHandler opponent = clientHandler.getOpponent();
        if (opponent != null) {
            // Kiểm tra xem đối thủ có trong danh sách yêu cầu tái đấu không
            if (rematchRequests.contains(opponent)) {
                // Đối thủ đã gửi yêu cầu tái đấu, ghép cặp lại
                System.out.println("Rematch found!");
                rematchRequests.remove(opponent);
                startMatch(opponent, clientHandler, Message.MessageType.REMATCH);
            } else {
                // Thêm người chơi vào danh sách yêu cầu tái đấu
                rematchRequests.add(clientHandler);
            }
        }
    }

    public synchronized void cancelRematch(ClientHandler clientHandler) {
        // Xóa client khỏi danh sách yêu cầu tái đấu nếu có
        rematchRequests.remove(clientHandler);
    }

    private void startMatch(ClientHandler player1, ClientHandler player2, Message.MessageType messageType) throws InterruptedException {
        // Thiết lập thông tin người chơi và bắt đầu trận đấu mới
        player1.setOpponent(player2);
        player2.setOpponent(player1);
        player1.setOpponentUser(player2.getUser());
        player2.setOpponentUser(player1.getUser());

        player1.sendMessage(new Message(messageType, "WHITE"));
        player2.sendMessage(new Message(messageType, "BLACK"));
        Thread.sleep(500);
        User[] users1 = {player1.getUser(), player2.getUser()};
        User[] users2 = {player2.getUser(), player1.getUser()};
        player1.sendMessage(new Message(Message.MessageType.INFORMATION_MATCH, users1));
        player2.sendMessage(new Message(Message.MessageType.INFORMATION_MATCH, users2));
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized void cancelWaiting(ClientHandler clientHandler) {
        waitingPlayers.remove(clientHandler);
    }

    public ServerDatabaseHandler getServerDatabaseHandler() {
        return serverDatabaseHandler;
    }

}
