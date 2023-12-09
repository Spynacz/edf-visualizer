package org.fhdmma.edf;
import java.util.LinkedList;
import java.util.Scanner;
import java.sql.SQLException;

public class Main
{
    static LinkedList<TimeFrame> tfl;
    public static void main(String[] args) {
        tfl = new LinkedList<TimeFrame>();
        TimeFrame tf = new TimeFrame(getTasks());
        try {
            for(int i=0;i<29;i++) {
                tfl.add(tf);
                Database.addTimeFrame(tf);
                tf = new TimeFrame(tf);
            }
            Database.printTimeFrames();
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            try {
                Database.disconnect();
            } catch (SQLException e) {
                System.out.println(e);
            }

        }
    }

    static private LinkedList<Task> getTasks() {
        Task t;
        LinkedList<Task> l = new LinkedList<>();
        int task_number = 0;
        int duration;
        int period;
        try {
            Database.connect();
            Scanner s = new Scanner(System.in);
            System.out.println("Task number:");
            task_number = s.nextInt();
            for(int i=0;i<task_number;i++) {
                System.out.printf("Task number %d:\n", i);
                System.out.println("Duration:");
                duration = s.nextInt();
                System.out.println("Period:");
                period = s.nextInt();
                t = new Task(i, duration, period);
                l.add(t);
                Database.addTask(t);
            }
            Database.printTasks();
        } catch (SQLException e) {
            System.out.println(e);

        }
        return l;
    }
}
