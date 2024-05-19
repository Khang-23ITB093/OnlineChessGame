package org.example.onlinechessgame.pieces;

import org.example.onlinechessgame.Board;
import org.example.onlinechessgame.Tile;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(PieceType type, boolean isWhite) {
        super(type, isWhite);
    }

    @Override
    public List<Tile> getPossibleMoves(Board board, Tile currentTile) {
        List<Tile> possibleMoves = new ArrayList<>();
        int[][] DIRECTIONS = {
                {0,-1}, {0, 1}, {-1, 0}, {1, 0}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
        for (int[] direction : DIRECTIONS) {
            int newRow = currentTile.getRow() + direction[0];
            int newCol = currentTile.getCol() + direction[1];

            while (isValidMove(board, newRow, newCol)) {
                possibleMoves.add(board.getTile(newRow, newCol));

                Tile destinationTile = board.getTile(newRow, newCol);
                if (destinationTile.hasPiece()) {
                    break; // Dừng nếu gặp quân cờ
                }

                newRow += direction[0];
                newCol += direction[1];
            }
        }

        return possibleMoves;
    }

    @Override
    public List<Tile> getPossibleCaptureMoves(Board board, Tile currentTile) {
        List<Tile> possibleMoves = new ArrayList<>();
        int[][] DIRECTIONS = {
                {0,-1}, {0, 1}, {-1, 0}, {1, 0}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
        for (int[] direction : DIRECTIONS) {
            int newRow = currentTile.getRow() + direction[0];
            int newCol = currentTile.getCol() + direction[1];
            while (isValidCapture(board, newRow, newCol)) {
                possibleMoves.add(board.getTile(newRow, newCol));

                newRow += direction[0];
                newCol += direction[1];
            }
            // Nếu ô đích có quân cờ
            possibleMoves.add(board.getTile(newRow, newCol));
        }

        return possibleMoves;
    }
}
