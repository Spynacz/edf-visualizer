package org.fhdmma.edf;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ConnectException;

public final class Client {
    private static Socket socket = null;
    private static int port = 9999;
    private static String address = "localhost";
    private static DataOutputStream out;
    private static ObjectInputStream in;

    private Client() {}

    public static void connect() throws IOException, UnknownHostException {
        while(socket == null || !socket.isConnected()) {
            try {
                socket = new Socket("localhost", port);
                out = new DataOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

            } catch (ConnectException e) {}
        }
    }

    public static void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public static DataOutputStream getOutput() throws IOException, UnknownHostException {
        if(socket == null || !socket.isConnected())
            connect();
        return out;
    }

    public static ObjectInputStream getInput() throws IOException, UnknownHostException {
        if(socket == null || !socket.isConnected())
            connect();
        return in;
    }
}
