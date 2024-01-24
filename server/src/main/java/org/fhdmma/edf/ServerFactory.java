package org.fhdmma.edf;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ServerFactory implements Runnable {
    private ExecutorService exe;
    private List<ServerTask> futList = new ArrayList<>();

    public ServerFactory(ExecutorService e) {
        exe = e;
    }

    public void run() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(9999);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                Server s = new Server(socket.accept());
                ServerTask st = new ServerTask(s);
                futList.add(st);
                exe.execute(st);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
