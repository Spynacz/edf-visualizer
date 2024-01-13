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
import javax.security.auth.login.FailedLoginException;

import sqlite.connect.net.DatabaseHandler;

public class Server implements Closeable, Runnable {
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private DataInputStream in = null;
    private int user = -1;
    private ServerSocket ssocket = null;

    public Server(ServerSocket server) {
        ssocket = server;
    }

    public void run() {
        String line = "";
        List<TimeFrame.Action> changes = new LinkedList<>();
        TimeFrame tf = null;
        Task task = null;
        String[] split = null;
        String str = null;

        try {
            socket = ssocket.accept();
            System.out.println("Client connected");
            in = new DataInputStream(new BufferedInputStream(
                    socket.getInputStream()));
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            System.out.println("Client couldn't connect to server");
        }

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
                            out.writeObject(null);
                        } else {
                            split = line.substring(1).split(",");
                            task = new Task(Integer.parseInt(split[0]),
                                    Integer.parseInt(split[1]));
                            changes.add(new TimeFrame.AddTask(task));
                            out.writeObject(task);
                        }
                        break;
                    case 'r':
                        if (user == -1) {
                            System.out.println("User not logged in");
                        } else {
                            str = line.substring(1);
                            changes.add(new TimeFrame.RemoveTask(Long.parseLong(str)));
                        }
                        break;
                    case 'u':
                        split = line.substring(1).split(",");
                        try {
                            user = DatabaseHandler.userLogin(split[0], split[1]).getId();
                            tf = DatabaseHandler.getLatestTimeFrame(user);
                            System.out.println(tf);
                            if(tf == null) {
                                tf = new TimeFrame(user);
                            }
                            changes.clear();
                            out.writeObject("good");
                        } catch (FailedLoginException e) {
                            System.out.println("Wrong password");
                            out.writeObject("wrong_pass");
                        }
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
