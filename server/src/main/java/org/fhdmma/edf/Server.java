package org.fhdmma.edf;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.net.Socket;
import java.net.ServerSocket;

public final class Server {
    private static Socket socket = null;
    private static ServerSocket server = null;
    private static ObjectOutputStream out = null;
    private static DataInputStream in = null;
    private static int port = 9999;

    private Server() {}

    public static void start() throws IOException {
        server = new ServerSocket(port);
        socket = server.accept();
        in = new DataInputStream(new BufferedInputStream(
                    socket.getInputStream()));
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    public static DataInputStream getInput() {
        return in;
    }

    public static ObjectOutputStream getOutput() {
        return out;
    }

    public static void stop() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
