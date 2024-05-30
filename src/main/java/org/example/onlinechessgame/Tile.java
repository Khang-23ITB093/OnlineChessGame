package org.example.onlinechessgame;

import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.onlinechessgame.pieces.Piece;

import java.io.Serial;
import java.io.Serializable;

public class Tile extends StackPane implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    private Piece piece;
    private int row, col;
    private boolean isLight;

    public Tile(int row, int col, boolean isLight) {
        this.row = row;
        this.col = col;
        this.isLight = isLight;

        // Tạo hình chữ nhật làm nền cho ô cờ
        Rectangle background = new Rectangle(100, 100); // Kích thước ô cờ
        background.setFill(isLight ? Color.valueOf("#ebecd0") : Color.valueOf("#739552"));
        this.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        this.getChildren().add(background);
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        if (this.piece != null) {
            this.getChildren().remove(this.piece);
        }
        this.piece = piece;
        if (piece != null) {
            this.getChildren().add(piece);
        }
    }

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

    public boolean isLight() {
        return isLight;
    }

    public void setIsLight(boolean light) {
        isLight = light;
    }

    @Override
    public String toString() {
        return "Tile"+row+col;
    }


}
