package server;
import java.sql.*;
import java.util.Properties;
import java.io.*;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by Keno on 12/12/2016.
 */

public class DBManager {
    public static String username;
    String db_host, db_name, db_pass;
    Connection conn;


    // Constructor
    public DBManager() {
        try {
            Class.forName("org.h2.Driver");
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
            Statement st = conn.createStatement();
            String query;

            query = "SELECT * FROM Users WHERE UserName='" + username + "';";
            ResultSet rs = st.executeQuery(query);
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

            host = prop.getProperty("host"); //DB_HOst
            db_name = prop.getProperty("db_name"); //DB_Name
            db_pass = prop.getProperty("db_pass"); //DB_Pass

            Connection con = DriverManager.getConnection("jdbc:h2:~/test", db_name, db_pass);
            Statement st = con.createStatement();

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
     *
     * @param e
     */
    private void displaySQLErrors(SQLException e) {
        System.out.println("SQLException: " + e.getMessage() + "\n");
        System.out.println("SQLState: " + e.getSQLState() + "\n");
        System.out.println("VendorError: " + e.getErrorCode() + "\n");
    }


}
