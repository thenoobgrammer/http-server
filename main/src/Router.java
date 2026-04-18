import java.util.HashMap;
import java.util.List;

public class Router {
    HashMap<String, HttpHandler> routes = new HashMap<>();

    public Router() {
        routes.put("/health", (req, out) -> {
            String response = "HTTP/1.0 200 OK\r\n" +
                    "Content-Length: 2\r\n" +
                    "\r\n" +
                    "OK";
            out.write(response.getBytes());
        });
    }

    public List<String> GetEndpoints() {
        return routes.keySet().stream().toList();
    }

    public HttpHandler GetRoute(String endpoint) {
        return routes.get(endpoint);
    }

}
