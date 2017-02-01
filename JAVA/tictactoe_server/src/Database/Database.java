/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.io.File;
import java.io.InputStream;
import java.sql.*;

/**
 *
 * @author mohamed
 */
public final class Database {
    
    private static Connection connection;
    private static PreparedStatement insert;
    private static PreparedStatement uniqueUser;
    private static PreparedStatement existedUser;
    private static PreparedStatement select;
    private static PreparedStatement delete;
    private static PreparedStatement update;
    private static PreparedStatement updateWin;
    private static PreparedStatement updateLose;
    private static PreparedStatement saveGame;
    private static PreparedStatement updateDraw;
    private static PreparedStatement getSaveGame;
    
    private static Statement getAllUsernames;
    
    private static ResultSet selectedRecord;

    
    private Database() {
        
    }
    
    /**
     * Initiating the database connection
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    
    public static void init() throws ClassNotFoundException, SQLException {
        
        Class.forName("com.mysql.jdbc.Driver");
        
        connection = DriverManager.getConnection("jdbc:mysql://localhost/TicTacToe", "root", "5265023");
        
        insert = connection.prepareStatement("insert into Users(username, password, first_name, last_name) values(?, ?, ?, ?)");
        
        uniqueUser = connection.prepareStatement("select count(*) from Users where username = ?");
    
        existedUser = connection.prepareStatement("select count(*) from Users where username = ? and password = ?");
    
        select = connection.prepareStatement("select * from Users where username = ?");
        
        delete = connection.prepareStatement("delete from Users where username = ?");
        
        update = connection.prepareStatement("update Users set password = ? set first_name = ? set last_name = ? where username = ?");
        
        updateWin = connection.prepareStatement("update " + 
                                                " Users x inner join (select win from Users where username = ?) y "+
                                                " set x.win = y.win + 1 " + 
                                                " where x.username = ?");
        
        updateLose = connection.prepareStatement("update " + 
                                                " Users x inner join (select loss from Users where username = ?) y "+
                                                " set x.loss = y.loss + 1 " + 
                                                " where x.username = ?");
        
        updateDraw = connection.prepareStatement("update " + 
                                                " Users x inner join (select draw from Users where username = ?) y "+
                                                " set x.draw = y.draw + 1 " + 
                                                " where x.username = ?");
        
        saveGame = connection.prepareStatement("insert into SavedGame(id, saved_game, file_name)" + 
                                               " values((select id from Users where username = ?), ?, ?)");
                
        
        getSaveGame = connection.prepareStatement("select saved_game from SavedGame " +
                                                "where id = (select id from Users where username = ?) and file_name = ?");
        
        getAllUsernames = connection.createStatement();
    }   
    
    
    /**
     * Handles the insertion of a new record
     * @param username the username of the newly created user
     * @param password the password of the newly created user
     * @param firstName first name of the newly created user
     * @param lastName last name of the newly created user
     * @throws SQLException 
     */
    
    public static void insertRecord(String username, String password, String firstName, String lastName) throws SQLException {
        insert.setString(1, username);
        insert.setString(2, password);
        insert.setString(3, firstName);
        insert.setString(4, lastName);
        insert.execute();
    }
    
    
    /**
     * checks if the username already exists in database incase of multiple registration 
     * @param username
     * @return
     * @throws SQLException 
     */
    
    public static boolean checkUniqueness(String username) throws SQLException {
        uniqueUser.setString(1, username);
        ResultSet rs = uniqueUser.executeQuery();
        rs.next();
        return rs.getInt(1) == 0;
    }
    
    /**
     * check the user existence in database for login
     * @param username
     * @param password
     * @return
     * @throws SQLException 
     */
    
    public static boolean checkExistence(String username, String password) throws SQLException {
        existedUser.setString(1, username);
        existedUser.setString(2, password);
        ResultSet rs = existedUser.executeQuery();
        rs.next();
        return rs.getInt(1) == 1;
    }
    
    /**
     * select an entire record for a specific user
     * @param username A username
     * @return
     * @throws SQLException 
     */
    
    public static boolean selectUserInfo(String username) throws SQLException {
        select.setString(1, username);
        selectedRecord = select.executeQuery();
        return selectedRecord.next();
    }
    
    /**
     * gets a username for the selected record
     * @return String username
     * @throws SQLException 
     */
    
    public static String selectUsername() throws SQLException {
        return selectedRecord.getString("username");
    }
    
    /**
     * returns first name
     * @return String username
     * @throws SQLException 
     */
    
    public static String selectFirstName() throws SQLException {
        return selectedRecord.getString("first_name");
    }
    
    /**
     * returns last name for the selected record
     * @return String
     * @throws SQLException 
     */
    
    public static String selectLastName() throws SQLException {
        return selectedRecord.getString("last_name");
    }
    
    /**
     * gets the number of wins for the selected record
     * @return Integer
     * @throws SQLException 
     */
    
    public static int selectNumberOfWins() throws SQLException {
        return selectedRecord.getInt("win");
    }
    
    
    /**
     * gets the number of loss for the selected record
     * @return Integer
     * @throws SQLException 
     */
    
    public static int selectNumberOfLoss() throws SQLException {
        return selectedRecord.getInt("loss");
    }
    
    /**
     * gets the number of draw for the selected record
     * @return Integer
     * @throws SQLException 
     */
    
    public static int selectNumberOfDraw() throws SQLException {
        return selectedRecord.getInt("draw");
    }
    
    /**
     * Scrolls the selected records down 
     * @return boolean true if there's a new record
     * @throws SQLException 
     */
    
    public static boolean goNext() throws SQLException {
        return selectedRecord.next();
    }
    
    /**
     * execute select query for selecting all usernames
     * @throws SQLException 
     */
    
    public static void selectUsernames() throws SQLException {
        selectedRecord = getAllUsernames.executeQuery("select username from Users");
    }
    
    /**
     * Increases the number of wins by 1 for a specific user
     * @param username 
     * @throws SQLException 
     */
    
    public static void increaseWonGames(String username) throws SQLException {
        updateWin.setString(1, username);
        updateWin.setString(2, username);
        updateWin.execute();
    } 
    
    /**
     * Increase the number of losses by 1 for a specific user
     * @param username
     * @throws SQLException 
     */
    
    public static void increaseLostGames(String username) throws SQLException {
        updateLose.setString(1, username);
        updateLose.setString(2, username);
        updateLose.execute();
    }
    
    /**
     * Increase the number of draws by 1 for a specific user
     * @param username
     * @throws SQLException 
     */
    
    public static void increaseDrawGames(String username) throws SQLException {
        updateDraw.setString(1, username);
        updateDraw.setString(2, username);
        updateDraw.execute();
    }
    
    /**
     * Saves a game to a specific user
     * @param username 
     * @param gameMoves
     * @param fileName
     * @throws SQLException 
     */
    
    public static void saveGame(String username, String gameMoves, String fileName) throws SQLException {
        saveGame.setString(1, username);
        saveGame.setString(2, gameMoves);
        saveGame.setString(3, fileName);
        
        saveGame.execute();
        
    }
    
    /**
     * Returns a specific saved game for a specific user
     * @param username
     * @param file_name
     * @return String game moves that's saved
     * @throws SQLException 
     */
    
    public static String selectSaveGame(String username, String file_name) throws SQLException {
        getSaveGame.setString(1, username);
        getSaveGame.setString(2, file_name);
        ResultSet rs = getSaveGame.executeQuery();
        if(rs.next()) {
            return rs.getString("saved_game");
        }
        else {
            return null;
        }
    } 
    
    
}
