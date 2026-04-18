import java.util.Map;

public class Request {
    String method;
    String path;
    Map<String, String> headers;

    public Request(String method, String path, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.headers = headers;
    }
}
