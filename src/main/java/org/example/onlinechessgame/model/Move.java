package org.example.onlinechessgame.model;

import java.io.Serial;
import java.io.Serializable;

public class Move implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int startCol;
    private int startRow;
    private int endCol;
    private int endRow;

    public Move() {
    }

    public Move(int startRow, int startCol, int endRow, int endCol) {
        this.startCol = startCol;
        this.startRow = startRow;
        this.endCol = endCol;
        this.endRow = endRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getEndCol() {
        return endCol;
    }

    public int getEndRow() {
        return endRow;
    }

    @Override
    public String toString() {
        return "Move{" +
                "startCol=" + startCol +
                ", startRow=" + startRow +
                ", endCol=" + endCol +
                ", endRow=" + endRow +
                '}';
    }
}
