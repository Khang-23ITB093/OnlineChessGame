package org.example.onlinechessgame.pieces;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.onlinechessgame.Board;
import org.example.onlinechessgame.ChessApp;
import org.example.onlinechessgame.Tile;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public abstract class Piece extends ImageView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int row,col;
    private PieceType type;
    private boolean isWhite;
    private boolean hasMoved = false;
    public Piece(PieceType type, boolean isWhite) {
        super(new Image(String.valueOf(ChessApp.class.getResource(
                "/org/example/onlinechessgame/Images/" + (isWhite ? "white" : "black") +
                        type.toString().charAt(0) +
                        type.toString().substring(1).toLowerCase() +
                        ".png"))));
        this.setFitWidth(100);
        this.setFitHeight(100);
        this.type = type;
        this.isWhite =  isWhite;
    }
    public boolean hasMoved(){
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public PieceType getType() {
        return type;
    }

    public void setType(PieceType type) {
        this.type = type;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean white) {
        isWhite = white;
    }
    public abstract List<Tile> getPossibleMoves(Board board, Tile currentTile);

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
    public boolean isValidMove(Board board, Tile currentTile, int newRow, int newCol) {
        if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) {
            return false; // Ô nằm ngoài bàn cờ
        }
        Tile destinationTile = board.getTile(newRow, newCol);
        return (!destinationTile.hasPiece()) || (destinationTile.hasPiece() && destinationTile.getPiece().isWhite() != isWhite());
    }

    @Override
    public String toString() {
        return (isWhite?"White":"Black")+type+row+col;
    }
}
