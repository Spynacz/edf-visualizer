package org.fhdmma.edf;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

public class ServerFactory implements Runnable {
    private ExecutorService exe;
    private List<FutureTask<Integer>> futList = new ArrayList<>();

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
                FutureTask<Integer> ft = new FutureTask<Integer>(s) {

                    @Override
                    protected void done() {
                        try {
                            Integer res = this.get();
                            if (res == 1)
                                System.out.println("Client disconnected normally");
                            else if (res == -1)
                                System.out.println("Server encountered unknown error");
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                };
                futList.add(ft);
                exe.execute(ft);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
