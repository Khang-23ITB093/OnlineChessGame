package org.example.onlinechessgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.onlinechessgame.controllers.ChessBoardController;

public class ChessApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ChessApp.class.getResource("/org/example/onlinechessgame/controllers/ChessBoard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 816, 816);
        primaryStage.setTitle("Chess Game");
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        primaryStage.getIcons().add(new Image(String.valueOf(ChessApp.class.getResource("/org/example/onlinechessgame/Images/logo.png"))));
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        ChessBoardController controller = fxmlLoader.getController();
        Board board = new Board();
        controller.setBoard(board);
    }
}
