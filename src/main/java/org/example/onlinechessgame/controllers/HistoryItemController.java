package org.example.onlinechessgame.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.onlinechessgame.ChessApp;
import org.example.onlinechessgame.model.History;

import java.net.URL;
import java.time.temporal.TemporalField;
import java.util.ResourceBundle;

public class HistoryItemController{

    @FXML
    private ImageView opponentImageView;

    @FXML
    private Label opponentNameLabel;

    @FXML
    private ImageView resultImageView;

    @FXML
    private Label timeLabel;


    public void setHistory(History history, String username) {
        if (history.getUsernamePlayer1().equals(username)) {
            opponentNameLabel.setText(history.getUsernamePlayer2());
        } else {
            opponentNameLabel.setText(history.getUsernamePlayer1());
        }
        timeLabel.setText(String.valueOf(history.getLocalDateTime().getDayOfMonth())+"/"+String.valueOf(history.getLocalDateTime().getMonthValue())+"/"+String.valueOf(history.getLocalDateTime().getYear())+"\n"+String.valueOf(history.getLocalDateTime().toLocalTime()));
        if (history.getResult(username) == 1) {
            resultImageView.setImage(new Image(String.valueOf(ChessApp.class.getResource("/org/example/onlinechessgame/Images/win.png"))));
        } else if (history.getResult(username) == 0) {
            resultImageView.setImage(new Image(String.valueOf(ChessApp.class.getResource("/org/example/onlinechessgame/Images/draw-icon.png"))));
        } else {
            resultImageView.setImage(new Image(String.valueOf(ChessApp.class.getResource("/org/example/onlinechessgame/Images/lose.png"))));
        }

    }

}
