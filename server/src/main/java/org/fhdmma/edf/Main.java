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
        for(int i=0;i<29;i++) {
            tfl.add(tf);
            if(i==17) {
                tf.addTask(new Task(3, 1, 4));
            }
            if(i==20) {
                tf.removeTask(3);
            }
            System.out.println(tf);
            tf = new TimeFrame(tf);
        }
    }

    static private LinkedList<Task> getTasks() {
        Task t;
        LinkedList<Task> l = new LinkedList<>();
        int task_number = 0;
        int duration;
        int period;
        Scanner s = new Scanner(System.in, "UTF-8");
        try {
            Database.connect();
            System.out.println("Task number:");
            task_number = s.nextInt();
            for(int i=0;i<task_number;i++) {
                System.out.printf("Task number %d:%n", i);
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
        s.close();
        return l;
    }
}
