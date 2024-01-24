package org.fhdmma.edf;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ServerTask extends FutureTask<Integer> {

    public ServerTask(Callable<Integer> callable) {
        super(callable);
    }

    @Override
    protected void done() {
        try {
            Integer res = this.get();
            if (res == 1)
                System.out.println("Client disconnected normally");
            else if (res == -1)
                System.out.println("Server encountered an error");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
