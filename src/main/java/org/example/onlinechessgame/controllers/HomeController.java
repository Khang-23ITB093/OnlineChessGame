package org.example.onlinechessgame.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.onlinechessgame.ChessApp;
import org.example.onlinechessgame.model.client.Client;
import org.example.onlinechessgame.util.QuickLoginUtil;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private Pane homePane;

    @FXML
    private VBox profilePane;

    @FXML
    private Pane waitingPane;

    private Client client;
    private ChessBoardController controller;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void matching(){
        try {
            client.requestMatchmaking();
            waitingPane.setVisible(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cancelMatching(){
        try {
            client.requestCancelMatchmaking();
            waitingPane.setVisible(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setClient(Client client) {
        this.client = client;
        client.setHomeController(this);
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
            primaryStage.setResizable(true);
            primaryStage.setScene(scene);
            primaryStage.show();
            controller = fxmlLoader.getController();
            controller.setClient(client);
            controller.setHomeController(this);
            waitingPane.setVisible(false);
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
            primaryStage.setResizable(true);
            primaryStage.setScene(scene);
            primaryStage.show();
            client.requestExit();
            removeQuickLogin();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hide(){
        Stage stage = (Stage) homePane.getScene().getWindow();
        stage.hide();
    }

    public void close(){
        ((Stage) homePane.getScene().getWindow()).close();
    }

    public void show(){
        Stage stage = (Stage) homePane.getScene().getWindow();
        stage.show();
    }

    private void hiddenAllPane(){
        homePane.setVisible(false);
        profilePane.setVisible(false);
        waitingPane.setVisible(false);
    }

    public void showProfilePane(){
        hiddenAllPane();
        profilePane.setVisible(true);
    }

    public void backToHome(){
        hiddenAllPane();
        homePane.setVisible(true);
    }

    private void removeQuickLogin(){
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

}
