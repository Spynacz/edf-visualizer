package org.fhdmma.edf;
import java.util.LinkedList;
import java.util.Scanner;

public class Main
{
    static LinkedList<TimeFrame> tfl;
    public static void main(String[] args) {
        tfl = new LinkedList<TimeFrame>();
        TimeFrame tf = new TimeFrame(getTasks());
        for(int i=0;i<29;i++) {
            tfl.add(tf);
            System.out.println(tf);
            tf = new TimeFrame(tf);
        }
    }

    static private LinkedList<Task> getTasks() {
        LinkedList<Task> l = new LinkedList<>();
        int task_number = 0;
        int duration;
        int period;
        Scanner s = new Scanner(System.in);
        System.out.println("Task number:");
        task_number = s.nextInt();
        for(int i=0;i<task_number;i++) {
            System.out.printf("Task number %d:\n", i);
            System.out.println("Duration:");
            duration = s.nextInt();
            System.out.println("Period:");
            period = s.nextInt();
            l.add(new Task(duration, period));
        }
        return l;
    }
}
