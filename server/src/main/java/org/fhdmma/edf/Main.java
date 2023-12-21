package org.fhdmma.edf;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main
{
    public static void main(String[] args) {
        ExecutorService exe = Executors.newFixedThreadPool(10);
        try {
            var s = new ServerSocket(9999);
            for(int i=0;i<10;i++)
                exe.execute(new Server(s));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot start server");
        }
    }
}
