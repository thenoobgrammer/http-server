import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);

        while (true) {
            Socket conn = server.accept();
            if (conn.isConnected()) {
                new ThreadServerSocket(conn).start();
            }
        }
    }
}
