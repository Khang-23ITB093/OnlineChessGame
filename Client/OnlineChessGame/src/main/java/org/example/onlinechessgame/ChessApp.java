package org.example.onlinechessgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.onlinechessgame.controllers.ChessBoardController;
import org.example.onlinechessgame.controllers.LoginController;
import org.example.onlinechessgame.model.client.Client;

import java.io.IOException;

public class ChessApp extends Application {
    private Client client;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ChessApp.class.getResource("/org/example/onlinechessgame/controllers/Login.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 1300, 950);
//        Scene scene = new Scene(fxmlLoader.load(), 816, 816);
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        primaryStage.setTitle("Login");
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        primaryStage.getIcons().add(new Image(String.valueOf(ChessApp.class.getResource("/org/example/onlinechessgame/Images/logo.png"))));
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        LoginController loginController = fxmlLoader.getController();
        loginController.getClient().requestQuickLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
