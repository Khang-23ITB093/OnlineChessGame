package org.example.onlinechessgame.pieces;

import org.example.onlinechessgame.Board;
import org.example.onlinechessgame.Tile;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(PieceType type, boolean isWhite) {
        super(type, isWhite);
    }

    @Override
    public List<Tile> getPossibleMoves(Board board, Tile currentTile) {
        List<Tile> possibleMoves = new ArrayList<>();

        int[][] offsets = {
                {-2, -1}, {-2, 1},
                {-1, -2}, {-1, 2},
                {1, -2}, {1, 2},
                {2, -1}, {2, 1}
        };

        for (int[] offset : offsets) {
            int row = currentTile.getRow() + offset[0];
            int col = currentTile.getCol() + offset[1];

            if (isValidMove(board, row, col)) {
                possibleMoves.add(board.getTile(row, col));
            }
        }
        return possibleMoves;
    }

    @Override
    public List<Tile> getPossibleCaptureMoves(Board board, Tile currentTile) {
        List<Tile> possibleMoves = new ArrayList<>();

        int[][] offsets = {
                {-2, -1}, {-2, 1},
                {-1, -2}, {-1, 2},
                {1, -2}, {1, 2},
                {2, -1}, {2, 1}
        };

        for (int[] offset : offsets) {
            int row = currentTile.getRow() + offset[0];
            int col = currentTile.getCol() + offset[1];

            if (isValidCapture(board, row, col)) {
                possibleMoves.add(board.getTile(row, col));
            } else if (board.getTile(row, col) != null && board.getTile(row, col).hasPiece()) {
                possibleMoves.add(board.getTile(row, col));
            }
        }
        return possibleMoves;
    }
}
