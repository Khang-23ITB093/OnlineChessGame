package org.example.onlinechessgame.pieces;

import org.example.onlinechessgame.Board;
import org.example.onlinechessgame.Tile;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece{
    public Pawn(PieceType type, boolean isWhite) {
        super(type, isWhite);
    }

    @Override
    public List<Tile> getPossibleMoves(Board board, Tile currentTile) {
        List<Tile> possibleMoves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1;

        //Move forward 1 square
        Tile tileInFront= board.getTile(currentTile.getRow() + direction, currentTile.getCol());
        if (tileInFront != null && !tileInFront.hasPiece()) {
            possibleMoves.add(tileInFront);
            //Move forward 2 square
            if ((isWhite() && currentTile.getRow() == 6) || (!isWhite() && currentTile.getRow() == 1)) {
                Tile tileTwoInFront = board.getTile(currentTile.getRow() + 2 * direction, currentTile.getCol());
                if (tileTwoInFront != null && !tileTwoInFront.hasPiece()) {
                    possibleMoves.add(tileTwoInFront);
                }
            }
        }

        //diagonal move
        int[] diagonalOffsets = {-1, 1};
        for (int offset : diagonalOffsets) {
            Tile diagonalTile = board.getTile(currentTile.getRow() + direction, currentTile.getCol() + offset);
            if (diagonalTile != null && diagonalTile.hasPiece() && diagonalTile.getPiece().isWhite() != isWhite()) {
                possibleMoves.add(diagonalTile);
            }
        }

        //En passant move(bắt tốt qua đường)
        Tile enPassantTarget = board.getEnPassantTargetTile();
        if (enPassantTarget != null && Math.abs(enPassantTarget.getRow() - currentTile.getRow()) == 1
                && Math.abs(enPassantTarget.getCol() - currentTile.getCol()) == 1) {
            possibleMoves.add(enPassantTarget);
        }

        return possibleMoves;
    }

    @Override
    public List<Tile> getPossibleCaptureMoves(Board board, Tile currentTile) {
        List<Tile> possibleCapture = new ArrayList<>();
        int direction = isWhite() ? -1 : 1;
        int[] diagonalOffsets = {-1, 1};
        for (int offset : diagonalOffsets) {
            Tile diagonalTile = board.getTile(currentTile.getRow() + direction, currentTile.getCol() + offset);
            if (diagonalTile != null) {
                possibleCapture.add(diagonalTile);
            }
        }
        return possibleCapture;
    }

    public Piece promotePawn(PieceType promotionType) {
        if (promotionType == PieceType.PAWN || promotionType == PieceType.KING)
        {
            throw new IllegalArgumentException("Invalid promotion type for Pawn");
        }
        return createPiece(promotionType, isWhite());
    }

    // Phương thức trợ giúp để tạo Piece (có thể di chuyển vào Board hoặc một lớp Utility)
    private Piece createPiece(PieceType type, boolean isWhite) {
        switch (type) {
            case QUEEN: return new Queen(type, isWhite);
            case ROOK: return new Rook(type, isWhite);
            case BISHOP: return new Bishop(type, isWhite);
            case KNIGHT: return new Knight(type, isWhite);
            // ... (Các trường hợp khác) ...
            default: throw new IllegalArgumentException("Invalid piece type");
        }
    }
    public List<Tile> getDiagnolMoves(Board board, Tile currentTile) {
        List<Tile> possibleMoves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1;
        int[] diagonalOffsets = {-1, 1};
        for (int offset : diagonalOffsets) {
            Tile diagonalTile = board.getTile(currentTile.getRow() + direction, currentTile.getCol() + offset);
            if (diagonalTile != null && diagonalTile.hasPiece() && diagonalTile.getPiece().isWhite() != isWhite()) {
                possibleMoves.add(diagonalTile);
            }
        }
        return possibleMoves;
    }
}
