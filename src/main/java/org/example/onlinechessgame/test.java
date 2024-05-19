package org.example.onlinechessgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.onlinechessgame.controllers.ChessBoardController;

public class test extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ChessApp.class.getResource("/org/example/onlinechessgame/controllers/UI.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 816, 816);
        Scene scene = new Scene(fxmlLoader.load(), 1300, 950);
        primaryStage.setTitle("Chess Game!");
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        primaryStage.setResizable(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
