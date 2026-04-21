import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int PORT = 8080;
    private static final int MAX_THREAD_POOL = 5;

    public static void main(String[] args) throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREAD_POOL);

        ServerSocket server = new ServerSocket(PORT);

        while (true) {
            Socket conn = server.accept();
            if (conn.isConnected()) {
                pool.submit(() -> handleConnection(conn));
            }
        }
    }

    static void handleConnection(Socket conn) throws RuntimeException {
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

            var response = """
                    HTTP/1.1 200 OK
                    Content-Type: application/json
                    Content-Length: %d
                    """.formatted(Array.getLength(out));

            out.write(response.getBytes());
            out.flush();

            conn.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
