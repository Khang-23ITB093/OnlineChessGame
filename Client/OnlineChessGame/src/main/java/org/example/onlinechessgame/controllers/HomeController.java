package org.example.onlinechessgame.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.onlinechessgame.ChessApp;
import org.example.onlinechessgame.model.History;
import org.example.onlinechessgame.model.User;
import org.example.onlinechessgame.model.client.Client;
import org.example.onlinechessgame.util.QuickLoginUtil;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private Pane homePane;

    @FXML
    private VBox profilePane;

    @FXML
    private VBox containerHistoryItemVbox;

    @FXML
    private Pane waitingPane;

    @FXML
    private Pane historyPane;

    @FXML
    private Label timerLabel;

    @FXML
    private Label userUsernameLabel;

    @FXML
    private Label userEmailLabel;

    @FXML
    private Label userPointLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ScrollPane historyScrollPane;

    private User user;

    private Client client;
    private ChessBoardController controller;
    private Timeline countdownTimeline;
    private ArrayList<History> historyList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void matching() {
        try {
            client.requestMatchmaking();
            waitingPane.setVisible(true);
            homePane.setDisable(true);
            startCountdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cancelMatching() {
        try {
            client.requestCancelMatchmaking();
            waitingPane.setVisible(false);
            homePane.setDisable(false);
            stopCountdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setClient(Client client) throws IOException {
        this.client = client;
        client.setHomeController(this);
        client.getData();
    }

    public void matchSuccess(String message) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChessApp.class.getResource("/org/example/onlinechessgame/controllers/UI.fxml"));
            Stage primaryStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1300, 950);
//        Scene scene = new Scene(fxmlLoader.load(), 816, 816);
//            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            primaryStage.setTitle("Chess App");
            primaryStage.setOnCloseRequest(event -> {
                System.exit(0);
            });
            primaryStage.getIcons().add(new Image(String.valueOf(ChessApp.class.getResource("/org/example/onlinechessgame/Images/logo.png"))));
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
            controller = fxmlLoader.getController();
            controller.setClient(client);
            controller.setHomeController(this);
            waitingPane.setVisible(false);
            stopCountdown();
            homePane.setDisable(false);
            controller.startGame(message.equals("WHITE"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChessApp.class.getResource("/org/example/onlinechessgame/controllers/Login.fxml"));
            Stage primaryStage = new Stage();
//            Scene scene = new Scene(fxmlLoader.load(), 1300, 950);
//        Scene scene = new Scene(fxmlLoader.load(), 816, 816);
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            primaryStage.setTitle("Chess App");
            primaryStage.setOnCloseRequest(event -> {
                System.exit(0);
            });
            primaryStage.getIcons().add(new Image(String.valueOf(ChessApp.class.getResource("/org/example/onlinechessgame/Images/logo.png"))));
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
            client.requestExit();
            removeQuickLogin();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hide() {
        Stage stage = (Stage) homePane.getScene().getWindow();
        stage.hide();
    }

    public void close() {
        ((Stage) homePane.getScene().getWindow()).close();
    }

    public void show() {
        Stage stage = (Stage) homePane.getScene().getWindow();
        stage.show();
    }

    private void hiddenAllPane() {
        homePane.setVisible(false);
        profilePane.setVisible(false);
        waitingPane.setVisible(false);
        historyPane.setVisible(false);
    }

    public void showProfilePane() {
        hiddenAllPane();
        profilePane.setVisible(true);
    }

    public void showHistoryPane() {
        hiddenAllPane();
        historyPane.setVisible(true);
    }

    public void backToHome() {
        hiddenAllPane();
        homePane.setVisible(true);
    }

    private void removeQuickLogin() {
        Path path = Paths.get(QuickLoginUtil.filepath);
        if (Files.exists(path))
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public ChessBoardController getController() {
        return controller;
    }

    public void startCountdown() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String currentTimeText = timerLabel.getText().substring(6); // Lấy phần giây trong định dạng "00:00:20"
            int currentTime = Integer.parseInt(currentTimeText);
            currentTime--;
            String formattedTime = String.format("00:00:%02d", currentTime);
            timerLabel.setText(formattedTime);
            progressBar.setProgress(currentTime / 20.0);

            if (currentTime <= 0) {
                countdownTimeline.stop();
                cancelMatching();
            }
        }));

        countdownTimeline.setCycleCount(20);
        timerLabel.setText("00:00:20");
        progressBar.setProgress(1.0);
        countdownTimeline.play();
    }

    public void stopCountdown() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
    }

    public void setUser(User user) {
        this.user = user;
        userUsernameLabel.setText(user.getUsername());
        userEmailLabel.setText(user.getEmail());
        userPointLabel.setText(String.valueOf(user.getPoint()));
        getHistoryList();
    }

    public void getHistoryList() {
        try {
            client.requestGetHistory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateAfterMatch() throws IOException {
        client.getData();
    }

    public void setHistoryList(List<History> historyList) {
        containerHistoryItemVbox.getChildren().clear();
        this.historyList = (ArrayList<History>) historyList;
        for (int i = historyList.size() - 1; i >= 0; i--) {
            FXMLLoader fxmlLoader = new FXMLLoader(ChessApp.class.getResource("/org/example/onlinechessgame/controllers/HistoryItem.fxml"));
            try {
                HBox historyItem = fxmlLoader.load();
                HistoryItemController controller = fxmlLoader.getController();
                controller.setHistory(historyList.get(i), user.getUsername());
                containerHistoryItemVbox.getChildren().add(historyItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
