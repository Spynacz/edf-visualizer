package org.fhdmma.edf;
import java.io.IOException;
import java.io.EOFException;
import java.sql.SQLException;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.List;
import java.util.LinkedList;

public class Server implements Closeable, Runnable {
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private DataInputStream in = null;

    public Server(ServerSocket server) throws IOException {
        socket = server.accept();
        in = new DataInputStream(new BufferedInputStream(
                    socket.getInputStream()));
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    public void run() {
        String line = "";
        List<TimeFrame.Action> changes = new LinkedList<>();
        TimeFrame tf = null;
        int task_id;

        try {
            Database.connect();
            tf = Database.getLatestTimeFrame();
            task_id = Database.getLatestTask().getId();
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Database error, shutting down connection");
            try {
                if(Database.isValid())
                    Database.disconnect();
            } catch(SQLException err) {
                e.printStackTrace();
                System.out.println("Couldn't check DB connection - not closing");
            }
            try {
                close();
            } catch(IOException err) {
                e.printStackTrace();
                System.out.println("Couldn't close socket");
            }
            return;
        }

        while(!line.equals(";")) {
            try {
                line = in.readUTF();
                switch(line.charAt(0)) {
                    case 'n':
                        for(int i=0;i<Integer.parseInt(line.substring(1));i++) {
                            tf = new TimeFrame(tf, changes);
                            try {
                                Database.addTimeFrame(tf);
                            } catch(RuntimeException e) {
                                e.printStackTrace();
                                System.out.println("Couldn't add TimeFrame to DB");
                            }
                            changes.clear();
                            out.writeObject(tf);
                        }
                        break;
                    case 'a':
                        var a = line.substring(1).split(",");
                        changes.add(new TimeFrame.AddTask(
                                    new Task(task_id++,
                                        Integer.parseInt(a[0]),
                                        Integer.parseInt(a[1]))));
                        break;
                }
            } catch(EOFException e) {
                try {
                    try {
                        Database.disconnect();
                    } catch(SQLException err) {
                        err.printStackTrace();
                        System.out.println("Couldn't disconnect from DB");
                    }
                    close();
                } catch(IOException err) {
                    err.printStackTrace();
                }
                return;
            }catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
