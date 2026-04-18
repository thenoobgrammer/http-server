import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        Router router = new Router();

        while (true) {
            String response = "";
            Socket conn = server.accept();
            InputStream in = conn.getInputStream();
            OutputStream out = conn.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            HttpRequestParser parser = new HttpRequestParser();

            var request = parser.parse(reader);
            boolean found = router.GetEndpoints().stream().anyMatch(request.path::equals);
            if (found) {
                router.GetRoute(request.path).handle(request, out);
            }

            response = "HTTP/1.0 404 OK\r\n" +
                    "Content-Length: 2\r\n" +
                    "\r\n" +
                    "Not found";

            out.write(response.getBytes());
            out.flush();

            conn.close();
        }
    }
}
