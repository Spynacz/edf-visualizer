package org.fhdmma.edf;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sqlite.connect.net.DatabaseHandler;

public class Main {
    private static Queue<TimeFrame> saveList;

    public static void main(String[] args) {
        ExecutorService exe = Executors.newFixedThreadPool(10);
        saveList = new LinkedList<>();
        try {
            if(!DatabaseHandler.exists()) {
                DatabaseHandler.connect();
                DatabaseHandler.init();
            } else {
                DatabaseHandler.connect();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        exe.execute(new ServerFactory(exe));

        while(true) {
            if (!saveList.isEmpty()) {
                save();
            } else {
                try {
                    Thread.sleep(100);
                } catch(Exception e) {}
            }
        }
    }

    public static void saveTimeFrame(TimeFrame tf) {
        saveList.add(tf);
    }

    private static void save() {
        try {
            if(DatabaseHandler.isValid())
                DatabaseHandler.connect();
            DatabaseHandler.addTimeFrame(saveList.remove());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't add timeframe to DB");
        }
    }
}
