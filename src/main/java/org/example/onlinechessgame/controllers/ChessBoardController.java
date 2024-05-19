package org.example.onlinechessgame.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.onlinechessgame.Board;
import org.example.onlinechessgame.Tile;
import org.example.onlinechessgame.model.Move;
import org.example.onlinechessgame.model.client.Client;
import org.example.onlinechessgame.pieces.Pawn;
import org.example.onlinechessgame.pieces.Piece;
import org.example.onlinechessgame.pieces.PieceType;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChessBoardController implements Initializable {
    @FXML
    private GridPane gridPane;

    private Board board;
    private Tile selectedTile = null;
    private Piece selectedPiece = null;
    private Tile lastMoveStart = null;
    private Tile lastMoveEnd = null;
    private Client client;

    private boolean isMyTurn = false;
    private boolean playerColor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khởi tạo bàn cờ
        Board board = new Board();
        setBoard(board);

        // Kết nối với server
        client = new Client("localhost", 12345, this);
        try {
            client.connectToServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

    public void matching(){
        try {
            client.requestMatchmaking();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startGame(boolean color){
        playerColor = color;
        isMyTurn = color;
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
}