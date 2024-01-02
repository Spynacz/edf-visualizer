package org.fhdmma.edf;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {
    private static Socket socket;
    private static int port;
    private static DataOutputStream out;
    private static ObjectInputStream in;

    public void connect(String address, String username, String password) throws IOException, UnknownHostException {
        while (socket == null || !socket.isConnected()) {
            try {
                socket = new Socket(address, port);
                out = new DataOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

            } catch (ConnectException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Connected to " + address + ":" + port + " as " + username);
    }

    public static void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
