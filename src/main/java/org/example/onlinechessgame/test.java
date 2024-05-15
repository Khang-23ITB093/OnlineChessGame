package org.example.onlinechessgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class test extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ChessApp.class.getResource("/org/example/onlinechessgame/controllers/promotePawn.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 816, 816);
        primaryStage.setTitle("Chess Game!");
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        primaryStage.setResizable(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
