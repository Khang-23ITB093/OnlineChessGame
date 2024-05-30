package org.example.onlinechessgame.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.onlinechessgame.Board;
import org.example.onlinechessgame.ChessApp;
import org.example.onlinechessgame.Tile;
import org.example.onlinechessgame.model.Move;
import org.example.onlinechessgame.model.client.Client;
import org.example.onlinechessgame.pieces.Piece;
import org.example.onlinechessgame.pieces.PieceType;

import java.net.URL;
import java.util.ResourceBundle;

public class PromotePawnController implements Initializable {
    @FXML
    private Button knightButton;

    @FXML
    private Button bishopButton;

    @FXML
    private Button queenButton;

    @FXML
    private Button rookButton;

    private Board board;
    private Tile pawnTile;
    private Client client;
    private Move move;
    private ChessBoardController controller;

    public void setup(Board board, Move move, Client client, ChessBoardController chessBoardController) {
        this.move = move;
        this.pawnTile = board.getTile(move.getEndRow(), move.getEndCol());
        this.board = board;
        this.client = client;
        controller = chessBoardController;
        displayPromotionOptions(pawnTile);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    private void displayPromotionOptions(Tile pawnTile){
        Piece piece = pawnTile.getPiece();
        boolean isWhite = piece.isWhite();
            knightButton.setGraphic(new ImageView(new Image(String.valueOf(ChessApp.class.getResource(
                    "/org/example/onlinechessgame/Images/"+ (isWhite ? "white" : "black") + "Knight.png")))));
            bishopButton.setGraphic(new ImageView(new Image(String.valueOf(ChessApp.class.getResource(
                    "/org/example/onlinechessgame/Images/"+ (isWhite ? "white" : "black") + "Bishop.png")))));
            queenButton.setGraphic(new ImageView(new Image(String.valueOf(ChessApp.class.getResource(
                    "/org/example/onlinechessgame/Images/"+ (isWhite ? "white" : "black") + "Queen.png")))));
            rookButton.setGraphic(new ImageView(new Image(String.valueOf(ChessApp.class.getResource(
                    "/org/example/onlinechessgame/Images/"+ (isWhite ? "white" : "black") + "Rook.png")))));

        }
    public void promoteToKnight(){
        board.promotePawn(pawnTile, PieceType.KNIGHT);
        board.setPiece(pawnTile.getPiece(), pawnTile.getRow(), pawnTile.getCol());
        sendPromotePieceMessage(pawnTile);
        exit();
    }
    public void promoteToBishop(){
        board.promotePawn(pawnTile, PieceType.BISHOP);
        board.setPiece(pawnTile.getPiece(), pawnTile.getRow(), pawnTile.getCol());
        sendPromotePieceMessage(pawnTile);
        exit();
    }
    public void promoteToQueen(){
        board.promotePawn(pawnTile, PieceType.QUEEN);
        board.setPiece(pawnTile.getPiece(), pawnTile.getRow(), pawnTile.getCol());
        sendPromotePieceMessage(pawnTile);
        exit();
    }
    public void promoteToRook() {
        board.promotePawn(pawnTile, PieceType.ROOK);
        board.setPiece(pawnTile.getPiece(), pawnTile.getRow(), pawnTile.getCol());
        sendPromotePieceMessage(pawnTile);
        exit();
    }
    private void sendPromotePieceMessage(Tile pawnTile) {
        try {
            client.movePiece(move);
            client.promotePiece(pawnTile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void exit() {
        controller.startOpponentTurn(controller.getPlayerColor());
        Stage stage = (Stage)knightButton.getScene().getWindow();
        stage.close();
    }
}
