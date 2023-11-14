package org.fhdmma.edf;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private int port;
    private boolean listening;

    public Server(int port) {
        this.port = port;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        listening = true;
        try (ServerSocket socket = new ServerSocket(port)) {
            while (listening) {
                Socket conn = socket.accept();
                System.out.println(
                        "Connection received from " +
                                conn.getInetAddress() +
                                ":" + conn.getPort());
                new ClientHandler(conn);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
