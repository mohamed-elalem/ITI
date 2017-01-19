import java.io.InputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mohamed on 1/6/17.
 */
public interface RemoteInterface extends Remote {

    void addUsername(String username, RemoteFightInterface player) throws RemoteException;


    void removeUsername(String username, boolean game) throws RemoteException;

    boolean checkUserLogged(String username) throws RemoteException;

    ArrayList<String> getAllUsers(String username) throws RemoteException;

    void addUserToDatabase(String username, String password, String firstName, String lastName) throws RemoteException, SQLException;

    boolean checkUserExistence(String username, String password) throws RemoteException, SQLException;

    boolean checkUserUniqueness(String username) throws RemoteException, SQLException;

    boolean selectUserInfo(String username) throws SQLException, RemoteException;

    String selectUsername() throws SQLException, RemoteException;

    String selectFirstName() throws SQLException, RemoteException;

    String selectLastName() throws SQLException, RemoteException;

    int selectNumberOfWins() throws SQLException, RemoteException;

    int selectNumberOfLoss() throws SQLException, RemoteException;
    
    int selectNumberOfDraw() throws SQLException, RemoteException;

    void challenge(String player1Username, String player2Username) throws RemoteException;

    void challengeStatus(String player1Username, String player2Username, boolean status) throws RemoteException;
    
    void playMove(int x, int y, String username) throws RemoteException;
    
    void endGame(String username) throws RemoteException;
    
    void increaseWin(String username) throws RemoteException;
    
    void increaseLoss(String username) throws RemoteException;
    
    void increaseDraw(String username) throws RemoteException;
    
    void saveGame(String username, String gameMoves, String fileName) throws RemoteException;
    
    String getGame(String username, String file_name) throws RemoteException;
    
}
