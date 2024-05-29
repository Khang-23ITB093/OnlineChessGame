package org.example.onlinechessgame;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.onlinechessgame.controllers.PromotePawnController;
import org.example.onlinechessgame.pieces.*;

import java.io.Serial;
import java.io.Serializable;

public class Board {
    private GridPane gridPane;
    private Tile[][] board;
    private Tile enPassantTargetTile;
    private boolean whiteTurn = true;

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    public Board() {
        board = new Tile[8][8];
        // Khởi tạo các ô Tile
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = new Tile(row, col, (row + col) % 2 == 0);
            }
        }
    }

    public void initializePieces() {
        // Đặt quân cờ vào vị trí ban đầu
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean isWhite = row == 6 || row == 7; // Quân trắng ở hàng 6 và 7
                PieceType pieceType;
                if (row == 1 || row == 6) {
                    pieceType = PieceType.ROOK;
                } else if (row == 0 || row == 7) {
                    switch (col) {
                        case 0: case 7: pieceType = PieceType.ROOK; break;
                        case 1: case 6: pieceType = PieceType.KNIGHT; break;
                        case 2: case 5: pieceType = PieceType.BISHOP; break;
                        case 3: pieceType = PieceType.QUEEN; break;
                        case 4: pieceType = PieceType.KING; break;
                        default: throw new IllegalStateException("Unexpected column: " + col);
                    }
                } else {
                    continue; // Các hàng khác không có quân cờ
                }
                Piece piece;
                board[row][col].setPiece((piece = createPiece(pieceType, isWhite)));
                piece.setRow(row);
                piece.setCol(col);
            }
        }
    }

    public void deleteAllPiece(){
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                deletePiece(board[row][col]);
            }
        }
    }
    public void movePiece(Piece piece, Tile destination) {
        piece.setHasMoved(true);
        int oldRow = piece.getRow();
        int oldCol = piece.getCol();
        deletePiece(destination);
        board[oldRow][oldCol].setPiece(null);
        gridPane.getChildren().remove(piece);
        System.out.println("Delete "+piece.toString());

        piece.setRow(destination.getRow());
        piece.setCol(destination.getCol());
        destination.setPiece(piece);

        // Xóa quân cờ nếu là en passant
        if (destination == enPassantTargetTile){
            if (destination.getRow() == 2)
                deletePiece(board[destination.getRow()+1][destination.getCol()]);
            else deletePiece(board[destination.getRow()-1][destination.getCol()]);
        }

        gridPane.add(piece, destination.getCol(), destination.getRow());

        // Kiểm tra en passant
        if (piece instanceof Pawn && Math.abs(destination.getRow() - oldRow) == 2) {
            enPassantTargetTile = board[oldRow + (destination.getRow() - oldRow) / 2][oldCol];
        } else {
            enPassantTargetTile = null;
        }

        // Kiểm tra và reset enPassantTargetTile
        if (enPassantTargetTile != null && ((piece.isWhite() && !whiteTurn) || (!piece.isWhite() && whiteTurn))) {
            enPassantTargetTile = null;
        }

        // Castling Logic
        if (piece instanceof King && Math.abs(destination.getCol() - oldCol) == 2) {
            System.out.println("Castling move");
            // Perform castling move
            int rookStartCol = destination.getCol() == 6 ? 7 : 0;
            int rookEndCol = destination.getCol() == 6 ? 5 : 3;
            Tile rookTile = board[piece.getRow()][rookStartCol];
            movePiece(rookTile.getPiece(), board[piece.getRow()][rookEndCol]);
            whiteTurn = !whiteTurn;
        }

        // Reset enPassantTargetTile sau mỗi nước đi
        whiteTurn = !whiteTurn; // Đổi lượt chơi
    }

    public Tile getEnPassantTargetTile() {
        return enPassantTargetTile;
    }

    // Phương thức trợ giúp để tạo Piece
    private Piece createPiece(PieceType type, boolean isWhite) {
        switch (type) {
            case KING: return new King(type, isWhite);
            case QUEEN: return new Queen(type, isWhite);
            case ROOK: return new Rook(type, isWhite);
            case BISHOP: return new Bishop(type, isWhite);
            case KNIGHT: return new Knight(type, isWhite);
            case PAWN: return new Pawn(type, isWhite);
            default: throw new IllegalArgumentException("Invalid piece type");
        }
    }

    public Tile getTile(int row, int col) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            return null;
        }
        return board[row][col];
    }

    public void setPiece(Piece piece, int row, int col) {
        board[row][col].setPiece(piece);
        if (piece != null) {
            piece.setRow(row);
            piece.setCol(col);
            // Cập nhật giao diện nếu piece != null
            gridPane.add(piece, col, row);
        }
    }
    public void deletePiece(Tile destination) {
        if (!destination.hasPiece())
            return;
        gridPane.getChildren().remove(destination.getPiece());
        destination.setPiece(null);

    }


    public void promotePawn(Tile pawnTile, PieceType promotionType) {
        Piece promotedPiece = null;
        Tile currentTile = getTile(pawnTile.getRow(), pawnTile.getCol());
        if (currentTile.getPiece() instanceof Pawn)
            promotedPiece = ((Pawn)currentTile.getPiece()).promotePawn(promotionType);
        else promotedPiece = pawnTile.getPiece();
        deletePiece(currentTile);
        pawnTile.setPiece(promotedPiece);
        gridPane.add(promotedPiece, pawnTile.getCol(), pawnTile.getRow());
    }

    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }
}
