package org.example.onlinechessgame.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.onlinechessgame.Board;
import org.example.onlinechessgame.Tile;
import org.example.onlinechessgame.pieces.Piece;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khởi tạo bàn cờ (ví dụ)
        Board board = new Board();
        setBoard(board);

        // Xử lý sự kiện click chuột vào ô cờ
        gridPane.setOnMouseClicked(this::handleMouseClick);
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
        if (selectedPiece == null) {
            // Chưa có quân cờ nào được chọn
            if (clickedTile.hasPiece() && clickedTile.getPiece().isWhite() == board.isWhiteTurn()) {
                // Click vào quân cờ của người chơi hiện tại, chọn quân cờ
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
                if (isValidMove(selectedPiece, clickedTile)) {
                    // Nước đi hợp lệ, di chuyển quân cờ
                    board.movePiece(selectedPiece, clickedTile);

                    // Highlight nước đi vừa thực hiện
                    highlightMoved(selectedTile, clickedTile);
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


}