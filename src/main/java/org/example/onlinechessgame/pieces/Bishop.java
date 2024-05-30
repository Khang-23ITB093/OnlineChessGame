package org.example.onlinechessgame.pieces;

import org.example.onlinechessgame.Board;
import org.example.onlinechessgame.Tile;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece{
    public Bishop(PieceType type, boolean isWhite) {
        super(type, isWhite);
    }

    @Override
    public List<Tile> getPossibleMoves(Board board, Tile currentTile) {
        List<Tile> possibleMoves = new ArrayList<>();
        int[][] DIRECTIONS = {
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
        for (int[] direction : DIRECTIONS) {
            int newRow = currentTile.getRow() + direction[0];
            int newCol = currentTile.getCol() + direction[1];

            while (isValidMove(board, newRow, newCol)) {
                possibleMoves.add(board.getTile(newRow, newCol));

                Tile destinationTile = board.getTile(newRow, newCol);
                // If the destination tile already has a piece on it
                if (destinationTile.hasPiece()) {
                    // Stop checking further tiles in this direction
                    break;
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
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
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
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8 && board.getTile(newRow, newCol).hasPiece()) {
                possibleMoves.add(board.getTile(newRow, newCol));
                if (board.getTile(newRow, newCol).getPiece().getType() == PieceType.KING && board.getTile(newRow, newCol).getPiece().isWhite() != this.isWhite()) {
                    newRow += direction[0];
                    newCol += direction[1];
                    while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8 && isValidCapture(board, newRow, newCol)) {
                        possibleMoves.add(board.getTile(newRow, newCol));
                        newRow += direction[0];
                        newCol += direction[1];
                    }
                }
            }
        }

        return possibleMoves;
    }
}
