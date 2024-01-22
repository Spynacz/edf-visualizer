package org.fhdmma.edf;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.security.auth.login.FailedLoginException;

import sqlite.connect.net.DatabaseHandler;

public class Server implements Closeable, Callable<Integer> {
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private DataInputStream in = null;
    private long user = -1;

    public Server(Socket s) {
        socket = s;
    }

    public Integer call() {
        String line = "";
        List<TimeFrame.Action> changes = new LinkedList<>();
        TimeFrame tf = null;
        Task task = null;
        String[] split = null;
        String str = null;

        try {
            in = new DataInputStream(new BufferedInputStream(
                    socket.getInputStream()));
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
        }

        try {
            DatabaseHandler.connect();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                close();
            } catch (IOException err) {
                e.printStackTrace();
            }
            return -1;
        }

        while (!line.equals(";")) {
            try {
                line = in.readUTF();
                switch (line.charAt(0)) {
                    case 'n':
                        if (user == -1) {
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
                            out.writeObject(null);
                        } else {
                            split = line.substring(1).split(",");
                            task = new Task(split[0], Integer.parseInt(split[1]),
                                    Integer.parseInt(split[2]), user);
                            changes.add(new TimeFrame.AddTask(task));
                            out.writeObject(task);
                        }
                        break;
                    case 'r':
                        if (user == -1) {
                        } else {
                            str = line.substring(1);
                            changes.add(new TimeFrame.RemoveTask(Long.parseLong(str)));
                            DatabaseHandler.removeTask(Long.parseLong(str));
                        }
                        break;
                    case 'u':
                        split = line.substring(1).split(",");
                        try {
                            user = DatabaseHandler.userLogin(split[0], split[1]).getId();
                            tf = DatabaseHandler.getLatestTimeFrame(user);
                            if (tf == null) {
                                tf = new TimeFrame(user);
                            }
                            changes.clear();
                            out.writeObject("good");
                            List<Task> userTasks = DatabaseHandler.getUserTasks(user);
                            out.writeObject(userTasks);
                        } catch (FailedLoginException e) {
                            out.writeObject("wrong_pass");
                        }
                        break;
                }
            } catch (EOFException e) {
                try {
                    close();
                    return 1;
                } catch (IOException err) {
                    err.printStackTrace();
                }
                return -1;
            } catch (SocketException e) {
                return -1;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
