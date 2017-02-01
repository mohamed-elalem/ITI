import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by mohamed on 1/9/17.
 */
public class Player extends UnicastRemoteObject implements RemoteFightInterface {

    private TicTacToe player;
    private String playerSymbol;
    private String opponentSymbol;
    private boolean turn;
    
    Player(TicTacToe player) throws RemoteException {
        this.player = player;
    }

    @Override
    public void play(int x, int y) throws RemoteException {
        if(turn)
            player.playOnBoard(x, y, playerSymbol);
        else 
            player.playOnBoard(x, y, opponentSymbol);
    }

    @Override
    public void win() throws RemoteException {
        player.setGameWin();
    }

    @Override
    public void lose() throws RemoteException {
        player.setGameLose();
    }

    @Override
    public void draw() throws RemoteException {
        player.setGameDraw();
    }

    @Override
    public void removePlayerFromMenu(String username) throws RemoteException {
        player.removeUserFromMenu(username);
    }

    @Override
    public void addPlayerToMenu(String username) throws RemoteException {
        player.addUserToMenu(username);
    }

    @Override
    public void checkChallenge(String username) throws RemoteException {
        player.checkChallenge(username);
    }

    @Override
    public void setTurn(boolean turn) throws RemoteException {
        player.setTurn(turn);
        this.turn = turn;
    }
    
    @Override
    public void flipTurn() throws RemoteException {
        player.flipTurn();
        turn = !turn;
    }
    
    @Override
    public void setPlayerSymbol(String playerSymbol) throws RemoteException {
        this.playerSymbol = playerSymbol;
        
        if(playerSymbol.equals("X")) {
            opponentSymbol = new String("O");
        }
        else {
            opponentSymbol = new String("X");
        }
    }
    
    @Override
    public void startGameWindow(String player1Username, String player2Username) throws RemoteException {
        player.openGame(player1Username, player2Username);
    }

    @Override
    public String returnUsername() throws RemoteException {
        return player.getCurrentUserName();
    }

    @Override
    public void opponentClosed() throws RemoteException {
        player.terminate();
    }

    @Override
    public void challengeDenied() throws RemoteException {
        player.challengeDenied();
    }
}
