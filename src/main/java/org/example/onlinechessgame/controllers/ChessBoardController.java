package org.example.onlinechessgame.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.onlinechessgame.Board;
import org.example.onlinechessgame.Tile;
import org.example.onlinechessgame.model.Move;
import org.example.onlinechessgame.model.User;
import org.example.onlinechessgame.model.client.Client;
import org.example.onlinechessgame.pieces.Piece;
import org.example.onlinechessgame.pieces.PieceType;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChessBoardController implements Initializable {
    @FXML
    private GridPane gridPane;
    @FXML
    private Pane resultPane;
    @FXML
    private Label namePlayer1Label;
    @FXML
    private Label namePlayer2Label;
    @FXML
    private Label winnerLabel;
    @FXML
    private Pane waitingPane;

    private Board board;
    private Tile selectedTile = null;
    private Piece selectedPiece = null;
    private Tile lastMoveStart = null;
    private Tile lastMoveEnd = null;
    private HomeController homeController;
    private Client client;

    private boolean isMyTurn = false;
    private boolean playerColor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khởi tạo bàn cờ
        Board board = new Board();
        setBoard(board);

        // Xử lý sự kiện click chuột vào ô cờ
        gridPane.setOnMouseClicked(this::handleMouseClick);
    }

    public void setBoard(Board board) {
        this.board = board;
        board.setGridPane(gridPane); // Truyền gridPane vào Board

        // Tạo các Tile và thêm vào GridPane
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = board.getTile(row, col);
                gridPane.add(tile, col, row); // Thêm Tile (StackPane) vào GridPane
            }
        }

        // Đặt các quân cờ vào vị trí ban đầu
        board.initializePieces();
    }


    private void handleMouseClick(MouseEvent event) {
        int col = (int) (event.getX() / 100);
        int row = (int) (event.getY() / 100);

        Tile clickedTile = board.getTile(row, col);
        System.out.println(clickedTile.getPiece());
        System.out.println(clickedTile);
        // 1. Kiểm tra ô được click
        if (clickedTile == null) {
            // Click chuột ra ngoài bàn cờ, bỏ qua
            return;
        }

        // 2. Xử lý chọn/bỏ chọn quân cờ
            // Chưa có quân cờ nào được chọn
        if (selectedPiece == null) {
                // Click vào quân cờ của người chơi hiện tại, chọn quân cờ
            if (clickedTile.hasPiece() && clickedTile.getPiece().isWhite() == board.isWhiteTurn() && clickedTile.getPiece().isWhite() == playerColor) {
                selectedPiece = clickedTile.getPiece();
                selectedTile = clickedTile;
                highlightPossibleMoves(selectedPiece);
                higlightTile(clickedTile);
            }
        } else {
            // Đã có quân cờ được chọn
            if (clickedTile == selectedTile) {
                // Click vào cùng một ô, bỏ chọn quân cờ
                selectedPiece = null;
                selectedTile = null;
                if (lastMoveStart != null && lastMoveEnd != null)
                    clearHighlightsPossibleMoves();
                else
                    clearHighlights();
            } else if (clickedTile.hasPiece() && clickedTile.getPiece().isWhite() == board.isWhiteTurn() && clickedTile != selectedTile) {
                // Click vào quân cờ của người chơi hiện tại, chọn quân cờ mới
                selectedPiece = clickedTile.getPiece();
                selectedTile = clickedTile;
                if (lastMoveStart != null && lastMoveEnd != null)
                    clearHighlightsPossibleMoves();
                else
                    clearHighlights();
                highlightPossibleMoves(selectedPiece);
                higlightTile(selectedTile);
            } else {
                // Click vào ô khác
                // 3. Xử lý di chuyển quân cờ
                if (isMyTurn && isValidMove(selectedPiece, clickedTile)) {


                    // Checkmate
                    if (clickedTile.hasPiece() && clickedTile.getPiece().getType() == PieceType.KING && selectedTile.getPiece().isWhite() != clickedTile.getPiece().isWhite()){
                        winning();
                    }
                    // Nước đi hợp lệ, di chuyển quân cờ
                    board.movePiece(selectedPiece, clickedTile);
                    //Pawn promotion
                    if (clickedTile!= null && clickedTile.getPiece().getType() == PieceType.PAWN && (clickedTile.getRow() == 0 || clickedTile.getRow() == 7)) {
                        if (isMyTurn) showPromotionPopup(createMove(selectedTile, clickedTile));
                    }
                    else {
                        // Gửi nước đi đến server
                        try {
                            client.movePiece(createMove(selectedTile, clickedTile));
                            System.out.println("Sent move to server! "+createMove(selectedTile, clickedTile).toString());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // Highlight nước đi vừa thực hiện
                    highlightMoved(selectedTile, clickedTile);

                    // Đánh dấu đã di chuyển quân cờ
                    isMyTurn = false;
                }
                    // Nước đi không hợp lệ, bỏ chọn quân cờ
                clearHighlightsPossibleMoves();
                selectedPiece = null;
                selectedTile = null;
            }
        }
    }

    private void highlightPossibleMoves(Piece piece) {
        List<Tile> possibleMoves = piece.getPossibleMoves(board, selectedTile);
        Glow glow = new Glow(0.5);
        for (Tile tile : possibleMoves) {
            if (tile != null) {
//                tile.getChildren().getFirst().setStyle(piece.isWhite() ? "-fx-fill: #eeeed2" : "-fx-fill: #769656;"); // Tô sáng nền của Tile
                tile.getChildren().getFirst().setStyle("-fx-fill: #f5f682;"); // Tô sáng nền của Tile
                tile.setEffect(glow);
            }
        }
    }

    private void clearHighlights() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = board.getTile(row, col);
                tile.getChildren().getFirst().setStyle(tile.isLight() ? "-fx-fill: #eeeed2" : "-fx-fill: #769656;");
                tile.setEffect(null);
            }
        }
    }

    private void clearHighlightsPossibleMoves(){
        clearHighlights();
        if (lastMoveStart != null)
            higlightTile(lastMoveStart);
        if (lastMoveEnd != null)
            higlightTile(lastMoveEnd);
    }
    private void higlightTile(Tile tile) {
        tile.getChildren().getFirst().setStyle("-fx-fill: #f5f682;"); // Tô sáng nền của Tile
        tile.setEffect(null);
    }

    public void highlightMoved(Tile startTile, Tile endTile) {
        // Clear any existing highlights
        clearHighlights();

        // Highlight the starting and ending tiles
        higlightTile(startTile);
        higlightTile(endTile);

        // Store the tiles for clearing later
        lastMoveStart = startTile;
        lastMoveEnd = endTile;
    }
    private boolean isValidMove(Piece piece, Tile destinationTile) {
        List<Tile> possibleMoves = piece.getPossibleMoves(board, selectedTile);
        return possibleMoves.contains(destinationTile);
    }

    private void showPromotionPopup(Move move) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/onlinechessgame/controllers/promotePawn.fxml"));
            Stage popup = new Stage();
            Scene scene = new Scene(loader.load());
            popup.setScene(scene);
            popup.setResizable(false);
            popup.initStyle(StageStyle.TRANSPARENT);
            popup.initModality(Modality.APPLICATION_MODAL);
            ((PromotePawnController)loader.getController()).setup(board, move, client);


            popup.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGame(boolean color){
        playerColor = color;
        isMyTurn = color;
        board.setWhiteTurn(true);
    }

    public void setInformationPlayers(User player, User opponent){
        if (playerColor){
            //White
            namePlayer1Label.setText(player.getUsername());
            namePlayer2Label.setText(opponent.getUsername());
        } else {
            //Black
            namePlayer1Label.setText(opponent.getUsername());
            namePlayer2Label.setText(player.getUsername());
        }
    }
    private Move createMove(Tile selectedTile, Tile clickedTile) {
        return new Move(selectedTile.getRow(), selectedTile.getCol(), clickedTile.getRow(), clickedTile.getCol());
    }
    public void opponentMovePiece(Move move) {
        Platform.runLater(() -> {
            Tile startTile = board.getTile(move.getStartRow(), move.getStartCol());
            Tile endTile = board.getTile(move.getEndRow(), move.getEndCol());
            board.movePiece(startTile.getPiece(), endTile);
            highlightMoved(startTile, endTile);
            isMyTurn = true;
        });
    }

    public Board getBoard() {
        return board;
    }

    public void setClient(Client client) {
        this.client = client;
        client.setController(this);
    }

    public void winning(){
        resultPane.setVisible(true);
        winnerLabel.setText((playerColor ? namePlayer1Label.getText() : namePlayer2Label.getText()) + " Won!");
        try {
            System.out.println("Sent win to server!");
            client.winGame();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void losing(){
        resultPane.setVisible(true);
        isMyTurn = false;
        winnerLabel.setText((!playerColor ? namePlayer1Label.getText() : namePlayer2Label.getText()) + " Won!");
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void close() throws IOException {
        Stage stage = (Stage) gridPane.getScene().getWindow();
        homeController.show();
        client.requestOut();
        stage.close();
    }

    public void rematch(){
        try {
            client.requestRematch();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        waitingPane.setVisible(true);
    }

    public void cancelRematch(){
        try {
            client.requestCancelRematch();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        waitingPane.setVisible(false);
    }

    public void newGame(boolean isWhite) {
        resetGameState(isWhite);
    }

    private void resetGameState(boolean isWhite) {
        // Đặt lại màu của các quân cờ
        board.deleteAllPiece();
        board.initializePieces();


        // Xóa lịch sử nước đi
        selectedTile = null;
        selectedPiece = null;
        lastMoveStart = null;
        lastMoveEnd = null;
        clearHighlights();

        // Đặt lại màu của người chơi
        // Đặt lại lượt chơi
        startGame(isWhite);

        // Đóng thông báo kết quả
        resultPane.setVisible(false);
        waitingPane.setVisible(false);
    }
}