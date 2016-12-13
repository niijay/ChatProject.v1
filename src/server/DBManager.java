package server;

import java.sql.*;
import java.util.Properties;
import java.io.*;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by Keno on 12/12/2016.
 */

public class DBManager {

    String db_host, db_name, db_pass;
    Connection connection;

    // Constructor
    public DBManager() {
        try {
            Class.forName("org.h2.Driver");

            init(); // < DB BOOTS.

        } catch (Exception e) {
            System.out.print("Unable to load h2 Driver");
            System.exit(1);
        }
    }

    /**
     * Check user name in DB
     */
    public boolean userExists(String username, String password) {
        try {
            PreparedStatement ps = null;
            String query;

            query = "SELECT * FROM Users WHERE UserName='" + username + "';";
            ResultSet rs = ps.executeQuery(query);
            if (rs.next()) {
                String readPass = rs.getString("Password");
                if (BCrypt.checkpw(password, readPass)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (SQLException se) {
            displaySQLErrors(se);
            return false;
        }
    }


    /***
     * Register an account.
     */

    public boolean registerUser(String username, String password) {
        try {
            PreparedStatement ps = null;
            String hashed_pw = BCrypt.hashpw(password, BCrypt.gensalt(10));
            String query = "INSERT INTO USERS VALUES('" + username + "','" + hashed_pw + "');";
            ResultSet rs = ps.executeQuery("SELECT * FROM Users WHERE UserName='" + username + "';");

            if (rs.next()) {
                return false; // If RS is next then we know the username is already registered...
            } else {
                ps.executeUpdate(query);
                return true;
            }
        } catch (SQLException ee) {
            ee.printStackTrace();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return true;
    }


    /**
     * Initiator, whenever connection needs to be established,s call this method.
     */
    private void init() {
        connectToDB();
    }

    public void connectToDB() {
        try {
            Properties prop = new Properties();
            FileInputStream in = new FileInputStream("properties.prop");
            prop.load(in);

            db_host = prop.getProperty("db_host"); //DB_HOst
            db_name = prop.getProperty("db_name"); //DB_Name
            db_pass = prop.getProperty("db_pass"); //DB_Pass

            Connection con = DriverManager.getConnection(db_host, db_name, db_pass);
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // FIXME: 12/12/2016
        } catch (SQLException se) {
            se.printStackTrace(); // FIXME: 12/12/2016
            System.out.println("Unable to connect to database");
            System.exit(1);
        } catch (IOException ie) {
            ie.printStackTrace(); // FIXME: 12/12/2016
        }
    }


    /**
     * Use this to save code writing, call when SQL Exception is triggered.
     */
    private void displaySQLErrors(SQLException e) {
        System.out.println("SQLException: " + e.getMessage() + "\n");
        System.out.println("SQLState: " + e.getSQLState() + "\n");
        System.out.println("VendorError: " + e.getErrorCode() + "\n");
    }


}
