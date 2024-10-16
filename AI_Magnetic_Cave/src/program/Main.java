/*
* Mahmoud Khatib 1200275
* Hamza Najar 1192605
*/
package program;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

    static final char EMPTY = ' ', BLACK = '□', WHITE = '■';
    static final int MAX_DEPTH = 7;
    static final int SHALLOW_DEPTH = 2;

    static char[][] board = new char[8][8];
    static char aiPlayer, humanPlayer;

    public static void main(String[] args) {
        initializeBoard();
        printBoard();

        Scanner scanner = new Scanner(System.in);
        char player = BLACK;
        int row, col;
        int option;

        System.out.println("Please choose a game mode:");
        System.out.println("1. manual entry for both Black’s moves and White’s moves");
        System.out.println("2. manual entry for Black’s moves & automatic moves for White");
        System.out.println("3. manual entry for White’s moves & automatic moves for Black");

        option = scanner.nextInt();

        humanPlayer = (option == 2)? BLACK : WHITE;
        aiPlayer = switchColor(humanPlayer);
        System.out.println(aiPlayer);

        while (true) {
            printBoard();

            if (player == humanPlayer || option == 1) { // or if the user has chosen the option to play both colors.
                System.out.println("Please enter the row: ");
                String in = scanner.next();
                row = Math.abs(Integer.parseInt(in) - 8);

                System.out.println("Please enter the column: ");
                in = scanner.next().toUpperCase();
                col = in.charAt(0) - 'A';
                System.out.println(row + " " + col);

                if (!validMove(row, col)) {
                    System.out.println("the previous move is invalid, please try again!!");
                    continue;
                }
                makeMove(player, row, col);
            } else { // AI turn
                double start = System.currentTimeMillis();
                int[] move = computerTurn();
                double end = System.currentTimeMillis();

                row = move[0];
                col = move[1];
                makeMove(aiPlayer, row, col);

                System.out.println("time: " + (end - start) + "ms");
            }
            if (checkWinner(row, col, player)) {
                System.out.println("The Winner is: " + player + " Congratulations!!");
                break;
            }
            else if (gameTie()) {
                System.out.println("Game Over, its a tie");
                break;
            }
            player = switchColor(player);
        }
        scanner.close();

        printBoard();
    } // end main

    public static void initializeBoard() {
        for (char[] chars : board)
            Arrays.fill(chars, EMPTY);
    }

    public static void printBoard() {
        int i = 8;
        System.out.print(" ");

        for (int j = 0; j < 8; j++)
            System.out.printf("%2c", (j + 65));
        System.out.println();

        for (char[] row : board) {
            System.out.print(i-- + "|");
            for (char cell : row)
                System.out.printf("%-2s", cell);
            System.out.println("|");
        }
    }

    public static boolean validMove(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7)
            return false;

        if (board[row][col] != EMPTY)
            return false;

        if (col != 7 && col != 0)
            return board[row][col + 1] != EMPTY || board[row][col - 1] != EMPTY;

        return true;
    }

    public static void makeMove(char player, int row, int col) {
        if (board[row][col] == EMPTY)
            board[row][col] = player;
    }

    public static void undoMove(int row, int col) {
        board[row][col] = EMPTY;
    }

    public static boolean checkWinner(int row, int col, char player) {
        int count = 0;

        // check horizontal.
        for (int i = -1; i >= -4; i--) {
            if (col + i < 8 && col + i >= 0) {
                if (board[row][col + i] == player)
                    count++;
                else
                    break;
            }
        }
        for (int i = 1; i <= 4; i++) {
            if (col + i < 8 && col + i >= 0) {
                if (board[row][col + i] == player)
                    count++;
                else
                    break;
            }
        }
        if (count >= 4)
            return true;

        // check vertically
        count = 0;
        for (int i = -1; i >= -4; i--) {
            if (row + i < 8 && row + i >= 0) {
                if (board[row + i][col] == player)
                    count++;
                else
                    break;
            }
        }
        for (int i = 1; i <= 4; i++) {
            if (row + i < 8 && row + i >= 0) {
                if (board[row + i][col] == player)
                    count++;
                else
                    break;
            }
        }
        if (count >= 4)
            return true;

        // check diagonal
        count = 0;
        for (int i = -1; i >= -4; i--) {
            if (row + i < 8 && row + i >= 0 && col + i < 8 && col + i >= 0)
                if (board[row + i][col + i] == player)
                    count++;
                else
                    break;
        }
        for (int i = 1; i <= 4; i++) {
            if (row + i < 8 && row + i >= 0 && col + i < 8 && col + i >= 0)
                if (board[row + i][col + i] == player)
                    count++;
                else
                    break;
        }
        if (count == 4)
            return true;

        // check 2nd diagonal
        count = 0;
        for (int i = -1; i >= -4; i--) {
            if (row + i < 8 && row + i >= 0 && col - i < 8 && col - i >= 0)
                if (board[row + i][col - i] == player)
                    count++;
                else
                    break;
        }
        for (int i = 1; i <= 4; i++) {
            if (row + i < 8 && row + i >= 0 && col - i < 8 && col - i >= 0)
                if (board[row + i][col - i] == player)
                    count++;
                else
                    break;
        }
        return count >= 4;
    }

    public static boolean gameTie() {
        for (char[] row : board) {
            for (char c : row)
                if (c == EMPTY)
                    return false;
        }
        return true;
    }

    public static int evaluate() {
        int score = 0, count = 0, stackBonus = 0;
        int enemyCount = 0, enemyStackBonus = 0;
        int emptyCount = 0;

        // check horizontal.
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == aiPlayer) {
                    score++;
                    stackBonus += count * count;
                    count++;

                    if (enemyCount != 0)
                        emptyCount = 0;

                    // reset counters and stack bonus for opponent.
                    enemyStackBonus = 0;
                    enemyCount = 0;
                } else if (board[i][j] == humanPlayer) {
                    score--;

                    enemyStackBonus -= enemyCount * enemyCount;
                    enemyCount++;

                    if (count != 0)
                        emptyCount = 0;

                    // reset the count and stack bonus for player.
                    count = 0;
                    stackBonus = 0;
                } else if (board[i][j] == EMPTY) {
                    emptyCount++;

                    if (emptyCount + count >= 5) {
                        score += stackBonus;
                        stackBonus = 0;
                    } else if (emptyCount + enemyCount >= 5) {
                        score += enemyStackBonus;
                        enemyStackBonus = 0;
                    }
                }
            }
            count = 0;
            enemyCount = 0;
            stackBonus = 0;
            enemyStackBonus = 0;
            emptyCount = 0;
        }
        // check vertically.
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[j][i] == aiPlayer) {
                    score++;
                    stackBonus += count * count;
                    count++;

                    if (enemyCount != 0)
                        emptyCount = 0;

                    // reset counters and stack bonus for opponent.
                    enemyStackBonus = 0;
                    enemyCount = 0;
                } else if (board[j][i] == humanPlayer) {
                    score--;

                    enemyStackBonus -= enemyCount * enemyCount;
                    enemyCount++;

                    if (count != 0)
                        emptyCount = 0;

                    // reset the count and stack bonus for player.
                    count = 0;
                    stackBonus = 0;
                } else if (board[j][i] == EMPTY) {
                    emptyCount++;

                    if (emptyCount + count >= 5) {
                        score += stackBonus;
                        stackBonus = 0;
                    } else if (emptyCount + enemyCount >= 5) {
                        score += enemyStackBonus;
                        enemyStackBonus = 0;
                    }
                }
            }
            count = 0;
            enemyCount = 0;
            stackBonus = 0;
            enemyStackBonus = 0;
            emptyCount = 0;
        }
        // check diagonal (top-left to bottom-right).
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8 - i; j++) {
                if (board[i + j][j] == aiPlayer) {
                    score++;
                    stackBonus += count * count;
                    count++;

                    if (enemyCount != 0)
                        emptyCount = 0;

                    // reset counters and stack bonus for opponent.
                    enemyStackBonus = 0;
                    enemyCount = 0;
                } else if (board[i + j][j] == humanPlayer) {
                    score--;

                    enemyStackBonus -= enemyCount * enemyCount;
                    enemyCount++;

                    if (count != 0)
                        emptyCount = 0;

                    // reset the count and stack bonus for player.
                    count = 0;
                    stackBonus = 0;
                } else if (board[i + j][j] == EMPTY) {
                    emptyCount++;

                    if (emptyCount + count >= 5) {
                        score += stackBonus;
                        stackBonus = 0;
                    } else if (emptyCount + enemyCount >= 5) {
                        score += enemyStackBonus;
                        enemyStackBonus = 0;
                    }
                }
            }
            count = 0;
            enemyCount = 0;
            stackBonus = 0;
            enemyStackBonus = 0;
            emptyCount = 0;
        }
        // check diagonal (top-right to bottom-left).
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j <= i; j++) {
                if (board[i - j][j] == aiPlayer) {
                    score++;
                    stackBonus += count * count;
                    count++;

                    if (enemyCount != 0)
                        emptyCount = 0;

                    // reset counters and stack bonus for opponent.
                    enemyStackBonus = 0;
                    enemyCount = 0;
                } else if (board[i - j][j] == humanPlayer) {
                    score--;

                    enemyStackBonus -= enemyCount * enemyCount;
                    enemyCount++;

                    if (count != 0)
                        emptyCount = 0;

                    // reset the count and stack bonus for player.
                    count = 0;
                    stackBonus = 0;
                } else if (board[i - j][j] == EMPTY) {
                    emptyCount++;

                    if (emptyCount + count >= 5) {
                        score += stackBonus;
                        stackBonus = 0;
                    } else if (emptyCount + enemyCount >= 5) {
                        score += enemyStackBonus;
                        enemyStackBonus = 0;
                    }
                }
            }
            count = 0;
            enemyCount = 0;
            stackBonus = 0;
            enemyStackBonus = 0;
            emptyCount = 0;
        }

        return score;
    }

    public static int[] computerTurn() {
        int row, col;
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
        int[] shallowBestMove = new int[2];
        int[] deepBestMove = new int[2];

        // search at a shallow depth
        int shallowScore = minimax(true, true, 0, aiPlayer, 0, 0, alpha, beta, shallowBestMove);
        System.out.println("minimax: " + shallowScore);

        if (shallowScore == 1000)
            return new int[]{shallowBestMove[0], shallowBestMove[1]};

        // search at a high depth.
        int deepScore = minimax(false, true, 0, aiPlayer, 0, 0, alpha, beta, deepBestMove);
        System.out.println("minimax: " + deepScore);

        if (shallowScore > deepScore) {
            System.out.println("bestScore: " + shallowScore);
            row = shallowBestMove[0];
            col = shallowBestMove[1];
            System.out.println(row + " " + col);
        } else {
            System.out.println("bestScore: " + deepScore);
            row = deepBestMove[0];
            col = deepBestMove[1];
            System.out.println(row + " " + col);
        }

        return new int[] {row, col};
    }

    public static int minimax(boolean shallowSearch, boolean isMaxPlayer, int depth, char player, int row, int col, int alpha, int beta, int[] bestMove) {
        if (depth != 0 && checkWinner(row, col, player)) {
            if (player == aiPlayer)
                return 1000;
            else if (player == humanPlayer)
                return -1000;
        }
        if (gameTie())
            return 0;

        if (depth == MAX_DEPTH || (shallowSearch && depth == SHALLOW_DEPTH))
            return evaluate();

        // if max player, then the children are minimizing.
        if (isMaxPlayer) {
            int max = Integer.MIN_VALUE;
            player = aiPlayer;

            for (int i = 8; i >= 0; i--) {
                boolean cutoffOccurred = false;

                for (int j = 0; j < 8; j++) {
                    if (validMove(i, j)) {
                        makeMove(player, i, j);
                        int score = minimax(shallowSearch, false, depth+1, player, i, j, alpha, beta, bestMove);
                        undoMove(i, j);

                        if (score > max) {
                            max = score;
                            if (depth == 0) {
                                bestMove[0] = i; // Update the best move row
                                bestMove[1] = j; // Update the best move column
                            }
                        }
                        alpha = Math.max(alpha, score);

                        if (beta <= alpha || score == 1000) {
                            cutoffOccurred = true;
                            break; // Beta cutoff
                        }
                    }
                }
                if (cutoffOccurred)
                    break;
            }
            return max;
        } else { // if the player is minimizing, then children are maximizing.
            int min = Integer.MAX_VALUE;
            player = humanPlayer;

            for (int i = 0; i < 8; i++) {
                boolean cutoffOccurred = false;

                for (int j = 0; j < 8; j++) {
                    if (validMove(i, j)) {
                        makeMove(player, i, j);
                        int score = minimax(shallowSearch, true, depth+1, player, i, j, alpha, beta, bestMove);
                        undoMove(i, j);

                        min = Math.min(min, score);
                        beta = Math.min(beta, score);

                        if (beta <= alpha || score == -1000) {
                            cutoffOccurred = true;
                            break; // Alpha cutoff
                        }
                    }
                }
                if (cutoffOccurred)
                    break;
            }
            return min;
        }
    }

    public static char switchColor(char player) {
        return (player == BLACK)? WHITE : BLACK;
    }

} // end class