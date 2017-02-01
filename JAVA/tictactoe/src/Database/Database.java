/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    private static Statement getAllUsernames;
    
    private static ResultSet selectedRecord;
    
    private Database() {
        
    }
    
    public static void init() throws ClassNotFoundException, SQLException {
        
        Class.forName("com.mysql.jdbc.Driver");
        
        connection = DriverManager.getConnection("jdbc:mysql://localhost/TicTacToe", "root", "5265023");
        
        insert = connection.prepareStatement("insert into Users(username, password, first_name, last_name) values(?, ?, ?, ?)");
        
        uniqueUser = connection.prepareStatement("select count(*) from Users where username = ?");
    
        existedUser = connection.prepareStatement("select count(*) from Users where username = ? and password = ?");
    
        select = connection.prepareStatement("select * from Users where username = ?");
        
        delete = connection.prepareStatement("delete from Users where username = ?");
        
        update = connection.prepareStatement("update Users set password = ? set first_name = ? set last_name = ? where username = ?");
        
        getAllUsernames = connection.createStatement();
    }   
    
    
    public static void insertRecord(String username, String password, String firstName, String lastName) throws SQLException {
        insert.setString(1, username);
        insert.setString(2, password);
        insert.setString(3, firstName);
        insert.setString(4, lastName);
        insert.execute();
    }
    
    public static boolean checkUniqueness(String username) throws SQLException {
        uniqueUser.setString(1, username);
        ResultSet rs = uniqueUser.executeQuery();
        rs.next();
        return rs.getInt(1) == 0;
    }
    
    public static boolean checkExistence(String username, String password) throws SQLException {
        existedUser.setString(1, username);
        existedUser.setString(2, password);
        ResultSet rs = existedUser.executeQuery();
        rs.next();
        return rs.getInt(1) == 1;
    }
    
    public static boolean selectUserInfo(String username) throws SQLException {
        select.setString(1, username);
        selectedRecord = select.executeQuery();
        return selectedRecord.next();
    }
    
    public static String selectUsername() throws SQLException {
        return selectedRecord.getString("username");
    }
    
    public static String selectFirstName() throws SQLException {
        return selectedRecord.getString("first_name");
    }
    
    public static String selectLastName() throws SQLException {
        return selectedRecord.getString("last_name");
    }
    
    public static int selectNumberOfWins() throws SQLException {
        return selectedRecord.getInt("win");
    }
    
    public static int selectNumberOfLoss() throws SQLException {
        return selectedRecord.getInt("loss");
    }
    
    public static boolean goNext() throws SQLException {
        return selectedRecord.next();
    }
    
    public static void selectUsernames() throws SQLException {
        selectedRecord = getAllUsernames.executeQuery("select username from Users");
    }
    
}
