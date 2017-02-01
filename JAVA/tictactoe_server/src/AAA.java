import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by mohamed on 1/7/17.
 */
public class AAA {

    private int x, y;

    private int [][] board;

    private int turn;

    private int used;

    public AAA() throws RemoteException {
        x = -1;
        y = -1;
        board = new int[3][3];
        turn = 0;
        used = 0;


        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                board[i][j] = 10;
            }
        }
    }



    private boolean checkWin(int score) {
        boolean row = false, col = false;
        int diagSum = 0, invDiagSum = 0;

        for(int i = 0; i < 3; i++) {
            int rowSum = 0;
            int colSum = 0;

            for(int j = 0; j < 3; j++) {
                rowSum += board[i][j];
                colSum += board[j][i];
            }
            diagSum += board[i][i];
            invDiagSum += board[i][2 - i];
            row |= (rowSum == score);
            col |= (colSum == score);

        }

        return row || col || (diagSum == score) || (invDiagSum == score);
    }

    private void printBoard() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                System.out.print(board[i][j] == 10 ? "-" : board[i][j] == 1 ? "X" : "O");
            }
            System.out.println();
        }
    }


}
