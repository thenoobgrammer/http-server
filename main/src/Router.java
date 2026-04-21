import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;

public class Router {
    HashMap<String, HttpHandler> routes = new HashMap<>();

    public Router() {
        routes.put("/login", (req, out) -> {
            var body = """
                    {
                        "message": "You successfully signed in!",
                        "error": null  
                    }
                    """;
            var response = """
                    HTTP/1.1 200 OK
                    Content-Type: application/json
                    Content-Length: %d
                    
                    %s
                    """.formatted(body.getBytes().length, body);

            out.write(response.getBytes());
        });

        routes.put("/slow", (req, out) -> {
            Thread.sleep(5000);
            var body = """
                    <!DOCTYPE html>
                    <html>
                        <body>
                            <h1>This was slow but we managed to get back to you!</h1>
                            <p>Send me something else</p>
                        </body>
                    </html>
                    """;

            var response = """
                    HTTP/1.1 200 OK
                    Content-Type: application/json
                    Content-Length: %d
                    
                    %s
                    """.formatted(body.getBytes().length, body);
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
