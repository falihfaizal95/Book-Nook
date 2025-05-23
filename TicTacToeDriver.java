import java.util.Scanner;

public class TicTacToeDriver {
//create a board 
    static char[][] board = new char [3][3];

    public static void main(String[] args) {
        initializeBoard();
        displayBoard();
        char currentPlayer = 'X';

    while (true) {
        playerMove(currentPlayer);
        displayBoard();

        if (checkWin(currentPlayer)) {
            System.out.println("Player " + currentPlayer + " wins!");
            break;
        }

        if (isBoardFull()) {
            System.out.println("It's a draw!");
            break;
        }

        // Switch to the other player
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';

    }
}
/*create blank spaces for the board; theres 3 columns and rows,
so make a loop 
*/
    public static void initializeBoard() {
        for(int i =0; i <3; i++) {
            for(int j = 0; j <3; j++) { 
                board [i][j] = ' ';
            }
        }
    }

    public static void displayBoard(){ 
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                System.out.print(board[i][j]);
                if (j < 2) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
            if (i <2) {
                System.out.println( "------------");

            }
        }

    }

    public static void playerMove(char player) {
        Scanner scanner = new Scanner(System.in);
        int row, col;
    
        while (true) {
            System.out.print("Player " + player + ", enter row (0-2): ");
            if((scanner.hasNextInt())) {
                row = scanner.nextInt();
            }
            else {
                System.out.println("Invalid input detected");
                scanner.next();
                continue; //break the loop
            }
    
            System.out.print("Player " + player + ", enter column (0-2): ");
            if((scanner.hasNextInt())) {
                col = scanner.nextInt();
            }
            else {
                System.out.println("Invalid Input");
                scanner.next();
                continue; //restart the loop
            }
    
            // Check if the chosen spot is empty and valid
            if (row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == ' ') {
                board[row][col] = player;
                break;
            } else {
                System.out.println("Invalid move. Try again.");
            }

        }
    }

    public static boolean isBoardFull() {
        for(int i=0; i<3; i++) {
            for (int j=0; j< 3; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
/*checking if there's a blank space; that means that there is a blank space where
there is more space to insert characters*/
            }
        }
        return true; //the board is full
    }

    public static boolean checkWin(char player) {
        //go through each row
        for(int i=0; i<3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true; //the row has all the same characters horizontally
            }
        }

        //go through each column 
        for(int j=0; j<3; j++) {
            if(board[0][j] == player && board[1][j] == player && board[2][j] == player) {
                return true;
            }
        }

        if(board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        else if(board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }
        else {
            return false;
        }
    }
}
