package org.fhdmma.edf;
import java.io.IOException;
import java.io.EOFException;
import java.util.List;
import java.util.LinkedList;


public class Main
{
    public static void main(String[] args) {
        TimeFrame tf = new TimeFrame();
        int task_id = 0;
        String line = "";
        List<TimeFrame.Action> changes = new LinkedList<>();

        try {
            Server.start();
            while(!line.equals(";")) {
                try {
                    line = Server.getInput().readUTF();
                    switch(line.charAt(0)) {
                        case 'n':
                            for(int i=0;i<Integer.parseInt(line.substring(1));i++) {
                                tf = new TimeFrame(tf, changes);
                                changes.clear();
                                System.out.println(tf);
                                Server.getOutput().writeObject(tf);
                            }
                            break;
                        case 'a':
                            var a = line.substring(1).split(",");
                            changes.add(new TimeFrame.AddTask(
                                        new Task(task_id++,
                                            Integer.parseInt(a[0]),
                                            Integer.parseInt(a[1]))));
                            break;
                    }
                } catch (EOFException e) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot start server");
        } finally {
            try {
                Server.stop();
            } catch(IOException e) {
                e.printStackTrace();
                System.out.println("Cannot stop server");
            }
        }
    }
}
