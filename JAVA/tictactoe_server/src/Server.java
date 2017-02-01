import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import Database.Database;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aliaa Halim, Mohamed El-Alem, Mohamed Shehata, Salma Gaber.
 */
public class Server extends UnicastRemoteObject implements RemoteInterface {

    private List<String> users;
    private HashMap<String, String> challenge;
    private HashMap<String, Game> ingame;
    private HashMap<String, RemoteFightInterface> players;
    private HashMap<String, String> playersUsernameInGame;

    private Registry reg;

    private int games;
    
    /**
     * Initiates the server
     * @throws RemoteException 
     */
    
    public Server() throws RemoteException {

        try {
            Database.init();
        } catch(SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        games = 0;

        users = new ArrayList<>();
        challenge = new HashMap<>();
        ingame = new HashMap<>();
        players = new HashMap<>();
        playersUsernameInGame = new HashMap<>();

        reg = LocateRegistry.createRegistry(8080);
        reg.rebind("server", this);
    }
    
    /**
     * ends the game for a specific user
     * @param username String
     * @throws RemoteException 
     */
    
    @Override
    public void endGame(String username) throws RemoteException {
        ingame.remove(username);
        playersUsernameInGame.remove(username);
        for(String user : users) {
            if(!user.equals(username)) {
                players.get(user).addPlayerToMenu(username);
            }
        }
    }
    
    /**
     * calls database to increase wins
     * @param username
     * @throws RemoteException 
     */

    @Override
    public void increaseWin(String username) throws RemoteException {
        try {
            Database.increaseWonGames(username);
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * calls database to increase losses
     * @param username
     * @throws RemoteException 
     */

    @Override
    public void increaseLoss(String username) throws RemoteException {
        try {
            Database.increaseLostGames(username);
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * calls database to increase draws
     * @param username
     * @throws RemoteException 
     */

    @Override
    public void increaseDraw(String username) throws RemoteException {
        try {
            Database.increaseDrawGames(username);
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * calls database to save a game
     * @param username
     * @param gameMoves
     * @param fileName
     * @throws RemoteException 
     */

    @Override
    public void saveGame(String username, String gameMoves, String fileName) throws RemoteException {
        try {
            Database.saveGame(username, gameMoves, fileName);
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     * @return Integer number of draws
     * @throws SQLException
     * @throws RemoteException 
     */
    
    @Override
    public int selectNumberOfDraw() throws SQLException, RemoteException {
        return Database.selectNumberOfDraw();
    }
    
    /**
     * calls database to select a saved game
     * @return String game moves
     * @throws RemoteException 
     */

    @Override
    public String getGame(String username, String file_name) throws RemoteException {
        try {
            return Database.selectSaveGame(username, file_name);
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private class Game {
        private RemoteFightInterface player1, player2;
        
        private int [][] board;


        private int used;

        /**
         * Initializes a new game for 2 players
         * @param player1 player1 callback object
         * @param player2 player2 callback object
         */

        public Game(RemoteFightInterface player1, RemoteFightInterface player2) {
            this.player1 = player1;
            this.player2 = player2;
            
            board = new int[3][3];
            
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    board[i][j] = 10;
                }
            }
        }
        
        /**
         * checks Winning
         * @param score
         * @return 
         */
        
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
        
        /**
         * 
         * @return Boolean true if player1 won 
         */
        
        public boolean checkPlayer1Win() {
            return checkWin(3);
        }
        
        
        /**
         * 
         * @return Boolean true if player2 won 
         */
        
        public boolean checkPlayer2Win() {
            return checkWin(0);
        }
        
        
        /**
         * 
         * @return Boolean true if draw 
         */
        
        public boolean checkDraw() {
            return used == 9;
        }
        
        /**
         * Plays a move for a specific player
         * @param x
         * @param y 
         */
        
        public void playMove(int x, int y) {
            try {
                board[x][y] = 1 - (used & 1);
                player1.play(x, y);
                player2.play(x, y);
                player1.flipTurn();
                player2.flipTurn();
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            used++;
            checkEndOfGame();
        }
        
        /**
         * Calls end of game
         */
        
        private void checkEndOfGame() {
            try {
                if(checkPlayer1Win()) {
                    player1.win();
                    player2.lose();
                }
                else if(checkPlayer2Win()) {
                    player1.lose();
                    player2.win();
                }
                else if(checkDraw()) {
                    player1.draw();
                    player2.draw();
                }
                
            } catch (RemoteException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Adds a new user to the online players
     * @param username String
     * @param player CallBack Object
     * @throws RemoteException 
     */

    @Override
    public synchronized void addUsername(String username, RemoteFightInterface player) throws RemoteException {
        System.out.println("Adding user " + username + " To all players");
        for(String user : users) {
            if(ingame.get(user) == null) {
                players.get(user).addPlayerToMenu(username);
                System.out.println("Added " + username + " to " + user + " " + players.get(user).returnUsername());
            }
        }
        users.add(username);
        players.put(username, player);
        
    }
    
    /**
     * Remove a user from the online players
     * @param username
     * @param game
     * @throws RemoteException 
     */


    @Override
    public synchronized void removeUsername(String username, boolean game) throws RemoteException {
        System.out.println("Removing user " + username);
        users.remove(username);
        if(playersUsernameInGame.get(username) != null) {
            for(String user : users) {
                if(!user.equals(playersUsernameInGame.get(username)))
                    players.get(user).addPlayerToMenu(playersUsernameInGame.get(username));
            }
            ingame.remove(playersUsernameInGame.get(username));
            players.get(playersUsernameInGame.get(username)).opponentClosed();
            playersUsernameInGame.remove(playersUsernameInGame.get(username));
            playersUsernameInGame.remove(username);
            
        }
        
        players.remove(username);
        
        ingame.remove(username);
        for(String user : users) {
            players.get(user).removePlayerFromMenu(username);
        }
    }
    
    /**
     * Check if user already logged
     * @param username
     * @return Boolean true if logged
     * @throws RemoteException 
     */

    @Override
    public boolean checkUserLogged(String username) throws RemoteException {
        return users.contains(username);
    }

    /**
     * Returns a list of all online users
     * @param username
     * @return List of usernames
     * @throws RemoteException 
     */
    
    @Override
    public synchronized ArrayList<String> getAllUsers(String username) throws RemoteException {
        ArrayList<String> cpy = new ArrayList<>();
        for(String user : users) {
            if(!user.equals(username) && ingame.get(user) == null) {
                cpy.add(user);
            }
        }
        return cpy;
    }
    
    /**
     * 
     * @param username
     * @param password
     * @param firstName
     * @param lastName
     * @throws RemoteException
     * @throws SQLException 
     */

    @Override
    public void addUserToDatabase(String username, String password, String firstName, String lastName) throws RemoteException, SQLException {
        Database.insertRecord(username, password, firstName, lastName);
    }

    /**
     * 
     * @param username
     * @param password
     * @return
     * @throws RemoteException
     * @throws SQLException 
     */
    
    @Override
    public boolean checkUserExistence(String username, String password) throws RemoteException, SQLException {
        return Database.checkExistence(username, password);
    }
    
    /**
     * 
     * @param username
     * @return
     * @throws RemoteException
     * @throws SQLException 
     */

    @Override
    public boolean checkUserUniqueness(String username) throws RemoteException, SQLException {
        return Database.checkUniqueness(username);
    }
    
    /**
     * 
     * @param username
     * @return
     * @throws SQLException
     * @throws RemoteException 
     */

    @Override
    public boolean selectUserInfo(String username) throws SQLException, RemoteException {
        return Database.selectUserInfo(username);
    }
    
    /**
     * 
     * @return
     * @throws SQLException
     * @throws RemoteException 
     */

    @Override
    public String selectUsername() throws SQLException, RemoteException {
        return Database.selectUsername();
    }
    
    /**
     * 
     * @return
     * @throws SQLException
     * @throws RemoteException 
     */

    @Override
    public String selectFirstName() throws SQLException, RemoteException {
        return Database.selectFirstName();
    }
    
    /**
     * 
     * @return
     * @throws SQLException
     * @throws RemoteException 
     */

    @Override
    public String selectLastName() throws SQLException, RemoteException {
        return Database.selectLastName();
    }
    
    /**
     * 
     * @return
     * @throws SQLException
     * @throws RemoteException 
     */

    @Override
    public int selectNumberOfWins() throws SQLException, RemoteException {
        return Database.selectNumberOfWins();
    }
    
    /**
     * 
     * @return
     * @throws SQLException
     * @throws RemoteException 
     */

    @Override
    public int selectNumberOfLoss() throws SQLException, RemoteException {
        return Database.selectNumberOfLoss();
    }
    
    /**
     * Sends a challenge to a specific player
     * @param player1Username
     * @param player2Username
     * @throws RemoteException 
     */

    @Override
    public void challenge(String player1Username, String player2Username) throws RemoteException {
        players.get(player2Username).checkChallenge(player1Username);
    }
    
    /**
     * Checks the challenge status
     * @param player1Username
     * @param player2Username
     * @param status
     * @throws RemoteException 
     */
    
    @Override
    public synchronized void challengeStatus(String player1Username, String player2Username, boolean status) throws RemoteException {
        if(status) {
            RemoteFightInterface player1 = players.get(player1Username);
            RemoteFightInterface player2 = players.get(player2Username);
            
            player1.startGameWindow(player1Username, player2Username);
            player2.startGameWindow(player1Username, player2Username);
            
            
            Game game = new Game(player1, player2);
            
            ingame.put(player1Username, game);
            ingame.put(player2Username, game);
            
            playersUsernameInGame.put(player1Username, player2Username);
            playersUsernameInGame.put(player2Username, player1Username);
            
            for(String user : users) {
                if(!user.equals(player1Username)) {
                    players.get(user).removePlayerFromMenu(player1Username);
                    System.err.println("Removing " + player2Username + " from " + user);
                }
                if(!user.equals(player2Username)) {
                    players.get(user).removePlayerFromMenu(player2Username);
                    System.err.println("Removing " + player2Username + " from " + user);
                }
            }
            
            player1.setTurn(true);
            player1.setPlayerSymbol("X");
            player2.setTurn(false);
            player2.setPlayerSymbol("O");
        }
        else {
            players.get(player1Username).challengeDenied();
        }
    }
    
    /**
     * plays a move on the board and sends it to both players
     * @param x
     * @param y
     * @param username
     * @throws RemoteException 
     */

    @Override
    public void playMove(int x, int y, String username) throws RemoteException {
        ingame.get(username).playMove(x, y);
    }


}
