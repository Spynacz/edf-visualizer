package org.fhdmma.edf;

import java.util.concurrent.ExecutorService;
import java.io.IOException;
import java.net.ServerSocket;

public class ServerFactory implements Runnable {
    private ExecutorService exe;

    public ServerFactory(ExecutorService e) {
       exe = e;
    }

    public void run() {
        ServerSocket socket = null;
        try {
        socket = new ServerSocket(9999);
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Couldn't create server");
            return;
        }
        while(true) {
            try {
                exe.execute(new Server(socket.accept()));
            } catch(IOException e) {
                System.out.println("Client couldn't connect to server");
            }
        }
    }
}
