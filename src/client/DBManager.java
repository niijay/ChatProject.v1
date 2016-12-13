package client;
import org.mindrot.jbcrypt.BCrypt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 ###Funtions of this class are :
 * -Set up connection to the database
 * -Connect to the db.
 * -Manage Login/Registration
 * -Get/Set Username
 * -Salt/DeSalt Login Credentials
 */
public class DBManager {
    public static String username;
    String db_host, db_name, db_pass;
    Connection conn;

    //Initiating the Database.#
    //FIXME: 12/12/2016 verify me later
    public DBManager(){
        try{
            Class.forName("org.h2.Driver");
        }
        catch (Exception ee){
            ee.printStackTrace();
            System.out.println("Unable to Connect to Load H2 Driver");
        }
    }

    //String to query the server for registration.
    public String selectUserQuery(String usernameIn){
        String compare = "SELECT * FROM USERS WHERE UserName = '" + usernameIn + "';";
        return compare;
    }

    public String registerQuery(String usernameIn, String passwordIn){
        String register = "INSERT INTO USERS VALUES('" + usernameIn + "','" + BCrypt.hashpw(passwordIn,BCrypt.gensalt(10)) + "');";
        return register;
    }

    public boolean registerUser(String usernameIn, String passwordIn){
        try {
            int n;

            //Getting the vars from .prop file
            db_credentilas();

            conn = DriverManager.getConnection(db_host, db_name, db_pass);
            Statement st = conn.createStatement();

            ResultSet result = st.executeQuery(selectUserQuery(usernameIn));

            if (result.next()){
                return false;
                //Put Test in here to show user exist already
            }else{
                n = st.executeUpdate(registerQuery(usernameIn,passwordIn));
                return  true;
            }
        }catch (SQLException ee) {
            ee.printStackTrace();
        }catch (Exception ee) {
            ee.printStackTrace();
        }
        return true;
    }

    public boolean loginUser(String usernameIn, String passwordIn) {
        try {
            db_credentilas();
            conn = DriverManager.getConnection(db_host, db_name, db_pass);
            Statement st = conn.createStatement();

            ResultSet rs  = st.executeQuery(selectUserQuery(usernameIn));

            if(rs.next()){
                String password = rs.getString("Password");
                if(deSalt(passwordIn,password)){
                    return true;
                    //Username & Password Matches
                }else{
                    return false;
                    //password is wrong.
                }
            }

            //Will be used to get username for display
            //e.g username has connected to chat room.
            username = usernameIn;

        }catch (SQLException ee){
            ee.printStackTrace();
        }catch (Exception ee){
            ee.printStackTrace();
        }
        return true;
    }

    public boolean deSalt(String passwordIn, String fromDBPass){
        boolean passChecked = BCrypt.checkpw(passwordIn,fromDBPass);
        if(passChecked == true) {
            return true;
        }
        return passChecked;
    }

    //connecting to the DB
    public void db_credentilas(){
        try {
            Properties prop = new Properties();
            FileInputStream in = new FileInputStream("properties.prop");
            prop.load(in);

            db_host = prop.getProperty("db_host");
            db_name = prop.getProperty("db_name");
            db_pass = prop.getProperty("db_pass");

        }catch (FileNotFoundException ee) {
            ee.printStackTrace();
            System.err.println("Cannot find Property File");
        } catch (IOException ee) {
            ee.printStackTrace();
        }
    }

    //Getters + Setters below this line.
    private final String getUsername(){
        return username;
    }

    public void closeConnection(){
        try {
            conn.close();
        } catch (Exception ee){
            ee.printStackTrace();
        }
    }
}