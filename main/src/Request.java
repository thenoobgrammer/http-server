import java.util.Map;

public class Request {
    String method;
    String path;
    Map<String, String> headers;
    String body;

    public Request(String method, String path, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }
}
