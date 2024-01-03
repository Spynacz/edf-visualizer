package org.fhdmma.edf;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {
    private static Socket socket;
    private static int port = 9999;
    private static DataOutputStream out;
    private static ObjectInputStream in;

    public void connect(String address, String username, String password) throws IOException, UnknownHostException {
        socket = new Socket(address, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        out.writeUTF("GETPASS" + username);
        String pass = "";
        try {
            pass = (String) in.readObject();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // TODO: Add password hashing (maybe)

        if (pass.equals(password)) {
            System.out.println("Connected to " + address + ":" + port + " as " + username);
        }
    }

    public static void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
