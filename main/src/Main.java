import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int PORT = 8080;
    private static final int MAX_THREAD_POOL = 5;

    private static ConcurrentHashMap<String, Socket> conns;
    private static ExecutorService pool;
    private static Router router;
    private static HttpRequestParser parser;
    private static ServerSocket server;

    public static void main(String[] args) throws IOException {
        server = new ServerSocket(PORT);
        conns = new ConcurrentHashMap<>();
        pool = Executors.newFixedThreadPool(MAX_THREAD_POOL);
        router = new Router();
        parser = new HttpRequestParser();

        while (true) {
            Socket conn = server.accept();
            if (conn.isConnected()) {
                pool.submit(() -> {
                    try {
                        handleConnection(conn);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    static void handleConnection(Socket conn) throws RuntimeException, IOException, InterruptedException {
        InputStream in = conn.getInputStream();
        OutputStream out = conn.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        conn.setSoTimeout(5_000);
        try {
            while (true) {
                var request = parser.parse(reader);
                if (request == null) break;

                boolean found = router.GetEndpoints().stream().anyMatch(request.path::equals);
                if (found) {
                    router.GetRoute(request.path).handle(request, out);
                }

                String connection = request.headers.get("Connection");
                if (connection != null && connection.equalsIgnoreCase("close")) {
                    break;
                }
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        out.flush();
        out.close();
        conn.close();
    }
}
