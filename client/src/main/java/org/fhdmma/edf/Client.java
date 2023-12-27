package org.fhdmma.edf;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ConnectException;

public final class Client {
    private static Socket socket;
    private static int port;
    private static String address;
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
        System.out.println("Connected to server");
    }

    public static void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public static void setPort(int p) { port = p; }
    public static void setHost(String a) { address = a; }

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
