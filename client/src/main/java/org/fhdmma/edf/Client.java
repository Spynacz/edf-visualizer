package org.fhdmma.edf;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.FailedLoginException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {
    private static Socket socket;
    private static int port = 9999;
    private static DataOutputStream out;
    private static ObjectInputStream in;
    private static List<Long> timeframes = new ArrayList<>();

    public static void connect(String address, String username, String password)
            throws IOException, UnknownHostException, FailedLoginException {
        socket = new Socket(address, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        out.writeUTF("u" + username + "," + password);

        String response = "";
        try {
            response = (String) in.readObject();
            if (response.equals("wrong_pass")) {
                throw new FailedLoginException("Wrong password");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        timeframes.clear();
    }

    public static void sendTask(Task task) {
        try {
            out.writeUTF("a" + task.getDuration() + "," + task.getPeriod());
            Task serverTask = (Task) in.readObject();
            Task retTask = new Task(serverTask.getId(), task.getName(), serverTask.getDuration(),
                    serverTask.getPeriod());
            Main.addTask(retTask);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void removeTask(Long id) {
        try {
            out.writeUTF("r" + id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Long> scheduleTasks(int num) throws IOException, ClassNotFoundException {
        out.writeUTF("n" + num);

        for (int i = 0; i < num; i++) {
            timeframes.add((Long) Client.getInput().readObject());
        }
        return timeframes;
    }

    public static ObjectInputStream getInput() {
        return in;
    }

    public static void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
