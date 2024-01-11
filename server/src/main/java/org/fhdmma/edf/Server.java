package org.fhdmma.edf;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import sqlite.connect.net.DatabaseHandler;

public class Server implements Closeable, Runnable {
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private DataInputStream in = null;
    private int user = -1;

    public Server(ServerSocket server) throws IOException {
        socket = server.accept();
        System.out.println("Client connected");
        in = new DataInputStream(new BufferedInputStream(
                socket.getInputStream()));
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    public void run() {
        String line = "";
        List<TimeFrame.Action> changes = new LinkedList<>();
        TimeFrame tf = null;

        try {
            DatabaseHandler.connect();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database error, shutting down connection");

            try {
                if (DatabaseHandler.isValid())
                    DatabaseHandler.disconnect();
            } catch (SQLException err) {
                e.printStackTrace();
                System.out.println("Couldn't check DB connection - not closing");
            }

            try {
                close();
            } catch (IOException err) {
                e.printStackTrace();
                System.out.println("Couldn't close socket");
            }
            return;
        }

        while (!line.equals(";")) {
            try {
                line = in.readUTF();
                switch (line.charAt(0)) {
                    case 'n':
                        if (user == -1) {
                            System.out.println("User not logged in");
                        } else {
                            for (int i = 0; i < Integer.parseInt(line.substring(1)); i++) {
                                tf = new TimeFrame(tf, changes);
                                Main.saveTimeFrame(tf);
                                changes.clear();
                                out.writeObject(tf);
                            }
                        }
                        break;
                    case 'a':
                        if (user == -1) {
                            System.out.println("User not logged in");
                        } else {
                            var a = line.substring(1).split(",");
                            changes.add(new TimeFrame.AddTask(
                                    new Task(Integer.parseInt(a[0]),
                                            Integer.parseInt(a[1]))));
                        }
                        break;
                    case 'u':
                        var u = line.substring(1).split(",");
                        user = DatabaseHandler.userLogin(u[0], u[1]).getId();
                        tf = new TimeFrame(user);
                        break;
                }
            } catch (EOFException e) {
                try {
                    try {
                        DatabaseHandler.disconnect();
                    } catch (SQLException err) {
                        err.printStackTrace();
                        System.out.println("Couldn't disconnect from DB");
                    }
                    close();
                } catch (IOException err) {
                    err.printStackTrace();
                }
                return;
            } catch (SocketException e) {
                System.out.println("Client disconnected");
                try {
                    DatabaseHandler.disconnect();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("Couldn't disconnect from DB");
                }
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
