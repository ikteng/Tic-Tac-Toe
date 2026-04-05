import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class TicTacToe {
    // ─── TicTacToePlayer (inner class) ───────────────────────────────────────
    static class TicTacToePlayer {

        private static final char[] PIECES = {'X', 'O'};

        char[][] board;
        char myPiece;
        char opp;

        TicTacToePlayer() {
            board = new char[3][3];
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    board[i][j] = ' ';

            myPiece = PIECES[new Random().nextInt(2)];
            opp = (myPiece == PIECES[0]) ? PIECES[1] : PIECES[0];
        }

        // ── make_move ─────────────────────────────────────────────────────────
        // Returns a list containing one (row, col) pair – the best move.
        List<int[]> makeMove(char[][] state) {
            double bestValue = Double.NEGATIVE_INFINITY;
            char[][] bestState = null;

            for (char[][] successor : succ(state, myPiece)) {
                double currentValue = minValue(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                                               successor, 1);
                if (currentValue > bestValue) {
                    bestValue = currentValue;
                    bestState = successor;
                }
            }

            List<int[]> move = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (state[i][j] == ' ' && bestState != null && state[i][j] != bestState[i][j]) {
                        move.add(0, new int[]{i, j});
                    }
                }
            }
            return move;
        }

        List<char[][]> succ(char[][] state, char piece) {
            List<char[][]> successors = new ArrayList<>();
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (state[row][col] == ' ') {
                        char[][] copy = deepCopy(state);
                        copy[row][col] = piece;
                        successors.add(copy);
                    }
                }
            }
            return successors;
        }

        // ── Minimax ───────────────────────────────────────────────────────────
        double maxValue(double a, double b, char[][] state, int depth) {
            if (gameValue(state) != 0) return gameValue(state);

            for (char[][] successor : succ(state, myPiece)) {
                a = Math.max(a, minValue(a, b, successor, depth + 1));
                if (a >= b) return b;  // alpha pruning
            }
            return a;
        }

        double minValue(double a, double b, char[][] state, int depth) {
            if (gameValue(state) != 0) return gameValue(state);

            for (char[][] successor : succ(state, opp)) {
                b = Math.min(b, maxValue(a, b, successor, depth + 1));
                if (a >= b) return a;  // beta pruning
            }
            return b;
        }

        void opponentMove(List<int[]> move) throws Exception {
            int row = move.get(0)[0];
            int col = move.get(0)[1];
            if (board[row][col] != ' ')
                throw new Exception("Illegal move detected");
            placePiece(move, opp);
        }

        void placePiece(List<int[]> move, char piece) {
            board[move.get(0)[0]][move.get(0)[1]] = piece;
        }

        void printBoard() {
            for (int row = 0; row < 3; row++) {
                StringBuilder line = new StringBuilder((row + 1) + " ");
                for (char cell : board[row])
                    line.append(cell).append(" ");
                System.out.println(line);
            }
            System.out.println("  1 2 3\n");
        }

        // Returns  1 if AI wins, -1 if player wins, 0 otherwise.
        int gameValue(char[][] state) {
            // Check rows
            for (int row = 0; row < 3; row++) {
                if (state[row][0] != ' ' &&
                    state[row][0] == state[row][1] && state[row][1] == state[row][2])
                    return (state[row][0] == myPiece) ? 1 : -1;
            }
            // Check columns
            for (int col = 0; col < 3; col++) {
                if (state[0][col] != ' ' &&
                    state[0][col] == state[1][col] && state[1][col] == state[2][col])
                    return (state[0][col] == myPiece) ? 1 : -1;
            }
            // Check diagonal '\'
            if (state[0][0] != ' ' &&
                state[0][0] == state[1][1] && state[1][1] == state[2][2])
                return (state[0][0] == myPiece) ? 1 : -1;
            // Check diagonal '/'
            if (state[0][2] != ' ' &&
                state[0][2] == state[1][1] && state[1][1] == state[2][0])
                return (state[0][2] == myPiece) ? 1 : -1;

            return 0;
        }

        private char[][] deepCopy(char[][] original) {
            char[][] copy = new char[3][3];
            for (int i = 0; i < 3; i++)
                copy[i] = original[i].clone();
            return copy;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Tic Tac Toe Game!");

        TicTacToePlayer ai = new TicTacToePlayer();
        int turn = 0;
        int totalPieces = 0;

        while (ai.gameValue(ai.board) == 0 && totalPieces < 9) {

            // AI's turn
            if (ai.myPiece == TicTacToePlayer.PIECES[turn]) {
                ai.printBoard();
                List<int[]> move = ai.makeMove(ai.board);
                ai.placePiece(move, ai.myPiece);
                System.out.printf("%c moved at (%d, %d)%n",
                                  ai.myPiece, move.get(0)[0], move.get(0)[1]);
                totalPieces++;

            // Human's turn
            } else {
                boolean moveMade = false;
                ai.printBoard();
                System.out.printf("%c's turn%n", ai.opp);

                while (!moveMade) {
                    System.out.print("Move (e.g. row,column): ");
                    String input = scanner.nextLine().trim();
                    String[] parts = input.split(",");

                    if (parts.length != 2) {
                        System.out.println("Invalid format. Use row,column (e.g. 2,3)");
                        continue;
                    }

                    int row, col;
                    try {
                        row = Integer.parseInt(parts[0].trim());
                        col = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter numbers only.");
                        continue;
                    }

                    if (row < 1 || row > 3 || col < 1 || col > 3) {
                        System.out.println("Row and column must be between 1 and 3.");
                        continue;
                    }

                    List<int[]> playerMove = new ArrayList<>();
                    playerMove.add(new int[]{row - 1, col - 1});

                    try {
                        ai.opponentMove(playerMove);
                        moveMade = true;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                totalPieces++;
            }

            turn = (turn + 1) % 2;
        }

        ai.printBoard();
        int result = ai.gameValue(ai.board);
        if (result == 1)       System.out.println("AI wins! Game over.");
        else if (result == -1) System.out.println("You win! Game over.");
        else                   System.out.println("It's a draw! Game over.");

        scanner.close();
    }
}