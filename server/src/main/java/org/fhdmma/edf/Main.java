package org.fhdmma.edf;
import java.io.IOException;
import java.sql.SQLException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.LinkedList;
import java.util.Queue;
import java.lang.InterruptedException;

public class Main
{
    private static Queue<TimeFrame> saveList;
    public static void main(String[] args) {
        ExecutorService exe = Executors.newFixedThreadPool(10);
        saveList = new LinkedList<>();
        try {
            var s = new ServerSocket(9999);
            for(int i=0;i<10;i++)
                exe.execute(new Server(s));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot start server");
        }
        while(true) {
            if(!saveList.isEmpty())
                save();
        }
    }

    public static void saveTimeFrame(TimeFrame tf) {
        saveList.add(tf);
    }

    private static void save() {
        try {
            if(!Database.isValid())
                Database.connect();
            Database.addTimeFrame(saveList.remove());
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't add timeframe to DB");
        }
    }
}
