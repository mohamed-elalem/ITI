import Database.Database;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mohamed on 1/6/17.
 */
public class TicTacToe {

    static {
        try {
            Database.init();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String [] args) {
        try {
            new Server();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
