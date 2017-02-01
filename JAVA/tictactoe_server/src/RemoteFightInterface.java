import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by mohamed on 1/9/17.
 */
public interface RemoteFightInterface extends Remote {

    void play(int x, int y) throws RemoteException;
    
    void win() throws RemoteException;
    
    void lose() throws RemoteException;
    
    void draw() throws RemoteException;
    
    void removePlayerFromMenu(String user) throws RemoteException;
    
    void addPlayerToMenu(String user) throws RemoteException;
    
    void checkChallenge(String username) throws RemoteException;
    
    void setTurn(boolean turn) throws RemoteException;
    
    void flipTurn() throws RemoteException;
    
    void setPlayerSymbol(String playerSymbol) throws RemoteException;
    
    void startGameWindow(String player1Username, String player2Username) throws RemoteException;
    
    String returnUsername() throws RemoteException;
   
    void opponentClosed() throws RemoteException;
    
    void challengeDenied() throws RemoteException;
}
