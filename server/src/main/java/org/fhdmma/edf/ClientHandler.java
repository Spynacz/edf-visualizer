package org.fhdmma.edf;

import java.io.IOException;
import java.net.Socket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
