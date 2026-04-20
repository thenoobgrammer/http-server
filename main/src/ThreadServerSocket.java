import java.io.*;
import java.net.Socket;

public class ThreadServerSocket extends Thread {
    private final Socket conn;

    public ThreadServerSocket(Socket conn) {
        this.conn = conn;
    }

    public void run() throws RuntimeException {
        try {
            System.out.println("Thread ID: " + Thread.currentThread().threadId());
            Router router = new Router();
            InputStream in = conn.getInputStream();
            OutputStream out = conn.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            HttpRequestParser parser = new HttpRequestParser();

            var request = parser.parse(reader);
            boolean found = router.GetEndpoints().stream().anyMatch(request.path::equals);
            if (found) {
                router.GetRoute(request.path).handle(request, out);
            }

            String response = "HTTP/1.0 404 OK\r\n" +
                    "Content-Length: 2\r\n" +
                    "\r\n" +
                    "Not found";

            out.write(response.getBytes());
            out.flush();

            conn.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}