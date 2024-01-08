package org.fhdmma.edf;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.BufferedInputStream;

public class Main {
    private static DataInputStream input = null;

    public static void main(String[] args) {
        input = new DataInputStream(new BufferedInputStream(System.in));
        Client.setHost("localhost");
        Client.setPort(9999);
        String line = "";
        while (!line.equals(";")) {
            try {
                line = input.readLine();
                switch(line.charAt(0)) {
                    case 'n':
                        Client.getOutput().writeUTF(line);
                        try {
                            for(int i=0;i<Integer.parseInt(line.substring(1));i++) {
                                Display.show((TimeFrame)Client.getInput().readObject());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Client.getOutput().writeUTF(line);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                Client.disconnect();
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        }
    }
}
