package org.example.onlinechessgame.pieces;

import org.example.onlinechessgame.Board;
import org.example.onlinechessgame.Tile;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece{
    public King(PieceType type, boolean isWhite) {
        super(type, isWhite);
    }

    @Override
    public List<Tile> getPossibleMoves(Board board, Tile currentTile) {
        List<Tile> possibleMoves = new ArrayList<>();
        List<Tile> opponentMoves = getOpponentMoves(board);
        int[][] offsets = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] offset : offsets) {
            int row = currentTile.getRow() + offset[0];
            int col = currentTile.getCol() + offset[1];

            if (isValidMove(board, row, col)) {
                    possibleMoves.add(board.getTile(row, col));
            }
        }

        // Castling Logic
        if (!hasMoved()) {
            // Check for castling on both sides (kingside and queenside)
            possibleMoves.add(checkCastling(board, currentTile, 7)); // Kingside
            possibleMoves.add(checkCastling(board, currentTile, 0 )); // Queenside
        }

        // Remove ilegal moves
        for (Tile opponentMove : opponentMoves) {
            if (opponentMove != null) {
                // if opponent moves contain King Tile remove Castling
                if (opponentMove.getRow() == currentTile.getRow() && opponentMove.getCol() == currentTile.getCol()) {
                    possibleMoves.remove(checkCastling(board, currentTile, 7));
                    possibleMoves.remove(checkCastling(board, currentTile, 0));
                } else {
                    possibleMoves.remove(opponentMove);
                }
            }
        }

        return possibleMoves;
    }

    private Tile checkCastling(Board board, Tile currentTile, int rookCol) {
        int row = currentTile.getRow();
        Tile rookTile = board.getTile(row, rookCol);
        if (rookTile != null && rookTile.hasPiece() && rookTile.getPiece().getType() == PieceType.ROOK && !rookTile.getPiece().hasMoved()) {
            boolean canCastle = true;
            int direction = rookCol == 7 ? 1 : -1; // Direction to check (kingside or queenside)
            for (int col = currentTile.getCol() + direction; col != rookCol; col += direction) {
                Tile tile = board.getTile(row, col);
                if (tile.hasPiece() ) {
                    canCastle = false;
                    break;
                }
            }
            if (canCastle) {
                return board.getTile(row, currentTile.getCol() + 2 * direction); // Add castling move
            }
        }
        return null;
    }

    // Helper method to check if a tile is threatened
    private List<Tile> getOpponentMoves(Board board) {
        // Lấy danh sách các nước đi của các quân cờ đối phương
        List<Tile> opponentMoves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile current = board.getTile(row, col);
                if (current.hasPiece() && current.getPiece().isWhite() != isWhite()) {
                    List<Tile> moves;
                    if (current.getPiece().getType() == PieceType.KING) {
                        moves = ((King) current.getPiece()).getPossibleMovesNonThreatened(board, current);
                    } else
                        moves = current.getPiece().getPossibleCaptureMoves(board, current);

                    opponentMoves.addAll(moves);
                }
            }
        }

        return opponentMoves;
    }

    public List<Tile> getPossibleMovesNonThreatened(Board board, Tile currentTile) {
        List<Tile> possibleMoves = new ArrayList<>();

        int[][] offsets = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] offset : offsets) {
            int row = currentTile.getRow() + offset[0];
            int col = currentTile.getCol() + offset[1];

            if (isValidMove(board, row, col)) {
                    possibleMoves.add(board.getTile(row, col));
            }
        }

        // Castling Logic
        if (!hasMoved()) {
            // Check for castling on both sides (kingside and queenside)
            possibleMoves.add(checkCastling(board, currentTile, 7)); // Kingside
            possibleMoves.add(checkCastling(board, currentTile, 0 )); // Queenside
        }

        return possibleMoves;
    }

    @Override
    public List<Tile> getPossibleCaptureMoves(Board board, Tile currentTile) {
        List<Tile> possibleMoves = new ArrayList<>();

        int[][] offsets = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}          , {0, 1},
                {1, -1} , {1, 0} , {1, 1}
        };

        for (int[] offset : offsets) {
            int row = currentTile.getRow() + offset[0];
            int col = currentTile.getCol() + offset[1];

            if (isValidCapture(board, row, col)) {
                possibleMoves.add(board.getTile(row, col));
            }else if (board.getTile(row, col).hasPiece()){
                possibleMoves.add(board.getTile(row, col));
            }
        }

        return possibleMoves;
    }
}
