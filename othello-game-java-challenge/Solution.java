import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Othello, also known as Reversi, is a game between two players, denoted by black
 * and white.
 *
 * Play happens on an 8x8 grid. Game pieces are discs with a black side and a
 * white side. The face-up side of a piece indicates its current owner.
 *
 * The game begins with two black pieces and two white pieces, as shown:
 *
 *   a b c d e f g h
 * 1
 * 2
 * 3
 * 4       B W
 * 5       W B
 * 6
 * 7
 * 8
 *
 * Players alternate turns, beginning with black.
 *
 * A player's turn consists of placing a new piece of their color on an empty space
 * and then flipping the opponent's pieces.
 *
 * A player flips lines of one or more opposing pieces when they are bookended
 * (surrounded) by the newly placed piece and one of their existing pieces. The line
 * including the bookends must be contiguous (no gaps). Lines of flipped pieces
 * can be othogonal or diagonal. Multiple lines may be flipped in a single turn.
 * (Note: One of the two surrounding pieces MUST be the newly placed piece.)
 *
 * For example, in the following game, black plays g6. This move flips the white
 * pieces at c6, d6, e6, f5, and f6 to black.
 *
 *   a b c d e f g h       a b c d e f g h       a b c d e f g h
 * 1                     1                     1
 * 2                     2                     2
 * 3       W B W         3       W B W         3       W B W
 * 4     W B B W B       4     W B B W B       4     W B B W B
 * 5   W B W B W         5   W B W B *         5   W B W B B
 * 6   B W W W W         6   B * * * * B       6   B B B B B B
 * 7                     7                     7
 * 8                     8                     8
 *
 * Every move must flip at least one piece. If a player cannot move, their turn is
 * skipped.
 *
 * For example, in the following game, white has no legal move:
 *
 *   a b c d e f g h
 * 1       W W W   W
 * 2     W W W W   W
 * 3   W W W B W W W
 * 4     W B B W B W
 * 5 W W W W W W B W
 * 6   W W W W W W W
 * 7     W W W W W W
 * 8 B B B B B B B W
 *
 * When neither player can move, the game ends.
 *
 * At the end of the game, the player with the most pieces wins. If players have
 * the same number of pieces, the game is a tie.
 *
 * Write a program that two people can use to play a game of Othello.
 *
 * A fully working program should:
 *   * validate attempted moves
 *   * execute moves
 *   * skip turns
 *   * end the game
 *   * display the winner
 *
 * If you have extra time, create a simple AI to play the game.
 *
 * Pace your development such that the program works as much as possible by the
 * end of the alloted time; i.e. it should not be in a "broken" state.
 *
 * The beginnings of a program are provided. Feel free to modify the program as desired.
 */

class Coordinate {
    public final int row;
    public final int col;

    public Coordinate(final int row, final int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Coordinate)) {
            return false;
        }
        final Coordinate coordinate = (Coordinate) other;
        return this.row == coordinate.row && this.col == coordinate.col;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.row, this.col});
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}

enum Color {
    BLACK,
    WHITE;

    public char abbreviation() {
        switch (this) {
            case BLACK:
                return 'B';
            case WHITE:
                return 'W';
        }
        throw new RuntimeException();
    }

    @Override
    public String toString() {
        switch (this) {
            case BLACK:
                return "black";
            case WHITE:
                return "white";
        }
        throw new RuntimeException();
    }
}

class Board {
    public final Color[][] positions;
    public final int size;

    public Board(final int size) {
        this.size = size;
        positions = new Color[size][];
        for (int row = 0; row < size; row++) {
            positions[row] = new Color[size];
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(' ');
        result.append(' ');
        for (int col = 0; col < size; col++) {
            result.append((char) ('a' + col));
            result.append(' ');
        }
        result.append('\n');
        for (int row = 0; row < size; row++) {
            result.append((char) ('1' + row));
            result.append(' ');
            for (int col = 0; col < size; col++) {
                Color position = positions[row][col];
                result.append(position != null ? position.abbreviation() : ' ');
                if (col < size - 1) {
                    result.append(' ');
                }
            }
            result.append('\n');
        }
        return result.toString();
    }
}

public class Solution {
    public static final int SIZE = 8;

    public static class CoordinateParseException extends Exception {
        static final long serialVersionUID = 1L;

        public CoordinateParseException(String message) {
            super(message);
        }
    }

    private static Coordinate parseCoordinate(String string) throws CoordinateParseException {
        if (string.length() != 2) {
            throw new CoordinateParseException("Input must be length 2");
        }
        int row = string.charAt(1) - '1';
        if (row < 0 || SIZE <= row) {
            throw new CoordinateParseException("Row out of bounds");
        }
        int col = string.charAt(0) - 'a';
        if (col < 0 || SIZE <= col) {
            throw new CoordinateParseException("Column out of bounds");
        }
        return new Coordinate(row, col);
    }

    void play(Scanner in) {
        Board board = new Board(SIZE);
        board.positions[3][3] = Color.BLACK;
        board.positions[3][4] = Color.WHITE;
        board.positions[4][3] = Color.WHITE;
        board.positions[4][4] = Color.BLACK;

        Color turn = Color.BLACK;

        while (true) {
            System.out.println(board);
            int blackCount = countPieces(board, Color.BLACK);
            int whiteCount = countPieces(board, Color.WHITE);
            System.out.println("Black: " + blackCount + " vs White: " + whiteCount);
            System.out.printf("Enter move for %s: ", turn);

            final Coordinate move;
            try {
                String input = in.nextLine();
                move = parseCoordinate(input);
            } catch (CoordinateParseException e) {
                System.out.println("Invalid move: " + e.getMessage());
                System.out.println();
                continue;
            } catch (NoSuchElementException e) {
                break;
            }

            // todo: implement below (brute force method)

            if (!validMove(board, move, turn)) {
                System.out.println("Invalid move\n");
                continue;
            } else {
                board.positions[move.row][move.col] = turn;
                board = flipPieces(board, move, turn);
            }

            boolean validMoveTurn = false;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (validMove(board, new Coordinate(i, j), turn)) {
                        validMoveTurn = true;
                        break;
                    }
                }
                if (validMoveTurn) break;
            }

            Color opp = turn == Color.BLACK ? Color.WHITE : Color.BLACK;
            boolean validMoveOpp = false;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (validMove(board, new Coordinate(i, j), opp)) {
                        validMoveOpp = true;
                        break;
                    }
                }
                if (validMoveOpp) break;
            }

            if (!validMoveTurn && !validMoveOpp) {
                System.out.println("Neither player has valid moves. Game over");

                blackCount = countPieces(board, Color.BLACK);
                whiteCount = countPieces(board, Color.WHITE);

                Color winner = blackCount > whiteCount ? Color.BLACK : Color.WHITE;
                if (winner == Color.BLACK) System.out.println("Winner: Black (" + blackCount + ") over (" + whiteCount + ")");
                else System.out.println("Winner: White (" + whiteCount + ") over (" + blackCount + ")");

                break;
            }
            else if (validMoveOpp) turn = opp;

            System.out.println();
        }
    }

    /**
     * Check if each of 8 directions (North, Northeast, East, Southeast, South, Southwest, West, Northwest)
     * has a sandwich structure (white pieces between black pieces, vice versa)
     */

    private boolean north(Board board, Coordinate move, Color turn, Color opp) {
        for (int i = 1; i < board.size; i++) {
            if (move.row - i < 0) break;
            Color curr = board.positions[move.row - i][move.col];
            if (curr != opp && curr != turn) break;
            else if (curr == turn && i == 1) break;
            else if (curr == turn && i > 1) return true;
        }
        return false;
    }

    private boolean northeast(Board board, Coordinate move, Color turn, Color opp) {
        for (int i = 1; i < board.size; i++) {
            if (move.row - i < 0 || move.col + i >= board.size) break;
            Color curr = board.positions[move.row - i][move.col + i];
            if (curr != opp && curr != turn) break;
            else if (curr == turn && i == 1) break;
            else if (curr == turn && i > 1) return true;
        }
        return false;
    }

    private boolean east(Board board, Coordinate move, Color turn, Color opp) {
        for (int i = 1; i < board.size; i++) {
            if (move.col + i >= board.size) break;
            Color curr = board.positions[move.row][move.col + i];
            if (curr != opp && curr != turn) break;
            else if (curr == turn && i == 1) break;
            else if (curr == turn && i > 1) return true;
        }
        return false;
    }

    private boolean southeast(Board board, Coordinate move, Color turn, Color opp) {
        for (int i = 1; i < board.size; i++) {
            if (move.row + i >= board.size || move.col + i >= board.size) break;
            Color curr = board.positions[move.row + i][move.col + i];
            if (curr != opp && curr != turn) break;
            else if (curr == turn && i == 1) break;
            else if (curr == turn && i > 1) return true;
        }
        return false;
    }

    private boolean south(Board board, Coordinate move, Color turn, Color opp) {
        for (int i = 1; i < board.size; i++) {
            if (move.row + i >= board.size) break;
            Color curr = board.positions[move.row + i][move.col];
            if (curr != opp && curr != turn) break;
            else if (curr == turn && i == 1) break;
            else if (curr == turn && i > 1) return true;
        }
        return false;
    }

    private boolean southwest(Board board, Coordinate move, Color turn, Color opp) {
        for (int i = 1; i < board.size; i++) {
            if (move.row + i >= board.size || move.col - i < 0) break;
            Color curr = board.positions[move.row + i][move.col - i];
            if (curr != opp && curr != turn) break;
            else if (curr == turn && i == 1) break;
            else if (curr == turn && i > 1) return true;
        }
        return false;
    }

    private boolean west(Board board, Coordinate move, Color turn, Color opp) {
        for (int i = 1; i < board.size; i++) {
            if (move.col - i < 0) break;
            Color curr = board.positions[move.row][move.col - i];
            if (curr != opp && curr != turn) break;
            else if (curr == turn && i == 1) break;
            else if (curr == turn && i > 1) return true;
        }
        return false;
    }

    private boolean northwest(Board board, Coordinate move, Color turn, Color opp) {
        for (int i = 1; i < board.size; i++) {
            if (move.row - i < 0 || move.col - i < 0) break;
            Color curr = board.positions[move.row - i][move.col - i];
            if (curr != opp && curr != turn) break;
            else if (curr == turn && i == 1) break;
            else if (curr == turn && i > 1) return true;
        }
        return false;
    }

    private boolean validMove(Board board, Coordinate move, Color turn) {
        if (board.positions[move.row][move.col] == Color.BLACK || board.positions[move.row][move.col] == Color.WHITE) {
            return false;
        }

        Color opp = turn == Color.BLACK ? Color.WHITE : Color.BLACK;
        boolean isValid = false;

        isValid = north(board, move, turn, opp);
        if (isValid) return true;

        isValid = northeast(board, move, turn, opp);
        if (isValid) return true;

        isValid = east(board, move, turn, opp);
        if (isValid) return true;

        isValid = southeast(board, move, turn, opp);
        if (isValid) return true;

        isValid = south(board, move, turn, opp);
        if (isValid) return true;

        isValid = southwest(board, move, turn, opp);
        if (isValid) return true;

        isValid = west(board, move, turn, opp);
        if (isValid) return true;

        isValid = northwest(board, move, turn, opp);
        if (isValid) return true;

        return false;
    }

    private Board flipPieces(Board board, Coordinate move, Color turn) {
        // strat: check in each direction of move for identical piece.
        // then replace all pieces in that direction up to that piece.

        Color opp = turn == Color.BLACK ? Color.WHITE : Color.BLACK;

        boolean updateNorth = north(board, move, turn, opp);
        boolean updateNortheast = northeast(board, move, turn, opp);
        boolean updateEast = east(board, move, turn, opp);
        boolean updateSoutheast = southeast(board, move, turn, opp);
        boolean updateSouth = south(board, move, turn, opp);
        boolean updateSouthwest = southwest(board, move, turn, opp);
        boolean updateWest = west(board, move, turn, opp);
        boolean updateNorthwest = northwest(board, move, turn, opp);

        if (updateNorth) {
            for (int i = 1; i < board.size; i++) {
                if (board.positions[move.row - i][move.col] == turn) break;
                board.positions[move.row - i][move.col] = turn;
            }
        }

        if (updateNortheast) {
            for (int i = 1; i < board.size; i++) {
                if (board.positions[move.row - i][move.col + i] == turn) break;
                board.positions[move.row - i][move.col + i] = turn;
            }
        }

        if (updateEast) {
            for (int i = 1; i < board.size; i++) {
                if (board.positions[move.row][move.col + i] == turn) break;
                board.positions[move.row][move.col + i] = turn;
            }
        }

        if (updateSoutheast) {
            for (int i = 1; i < board.size; i++) {
                if (board.positions[move.row + i][move.col + i] == turn) break;
                board.positions[move.row + i][move.col + i] = turn;
            }
        }

        if (updateSouth) {
            for (int i = 1; i < board.size; i++) {
                if (board.positions[move.row + i][move.col] == turn) break;
                board.positions[move.row + i][move.col] = turn;
            }
        }

        if (updateSouthwest) {
            for (int i = 1; i < board.size; i++) {
                if (board.positions[move.row + i][move.col - i] == turn) break;
                board.positions[move.row + i][move.col - i] = turn;
            }
        }

        if (updateWest) {
            for (int i = 1; i < board.size; i++) {
                if (board.positions[move.row][move.col - i] == turn) break;
                board.positions[move.row][move.col - i] = turn;
            }
        }

        if (updateNorthwest) {
            for (int i = 1; i < board.size; i++) {
                if (board.positions[move.row - i][move.col - i] == turn) break;
                board.positions[move.row - i][move.col - i] = turn;
            }
        }

        return board;
    }

    private int countPieces(Board board, Color player) {
        int playerTotal = 0;
        for (int i = 0; i < board.size; i++) {
            for (int j = 0; j < board.size; j++) {
                if (board.positions[i][j] == player) playerTotal++;
            }
        }
        return playerTotal;
    }

    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        final Solution othello = new Solution();
        try {
            othello.play(in);
        } finally {
            in.close();
        }
    }
}