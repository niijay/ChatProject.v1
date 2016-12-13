package server;

/**
 * UEL Chat Project by Ade, Godfrey,
 * Muhammad & Keno.
 */

import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class ServerHandler extends Thread {

    Socket connectSocket;
    Server server;
    PrintWriter out = null;
    BufferedReader in = null;
    String username;

    public ServerHandler(Socket socket, Server server) {
        connectSocket = socket;
        this.server = server;
    }

    /**
     * Going through the list and messaging every active connection...
     */
    public void sendToAllClients(String text) {
        for (int index = 0; index < server.clients.size(); index++) {
            ServerHandler sh = server.clients.get(index);
            sh.sendToClient(username.trim() + ": " + text);
        }
    }

    public synchronized void sendToClient(String text) {
        out.println(text);
    }

    public void run() {
        try {
            out = new PrintWriter(connectSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectSocket.getInputStream()));

            out.println("Enter your username: ");

            // FIXME: 12/12/2016  NEED TO ADD THE USERNAME CHECK HERE....

            synchronized (this) {
                username = in.readLine();
            }

            String line;
            synchronized (this) {
                while (true) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ee) {
                        ee.printStackTrace();
                    }
                    line = in.readLine();

                    if (line == null) {
                        break;
                    }
                    // Not needed but sends to the server..
                    System.out.println("Message to server from " + username + ": " + line);
                    sendToAllClients(line); // < Jumps to the method and sends this string.
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
        closeConnection();
    }

    /*
     * Lazy so writing this once ..
     */
    public void closeConnection() {
        try {
            in.close();
            out.close();
            connectSocket.close();

        } catch (IOException iii) {
            iii.printStackTrace();
        }
    }
}



