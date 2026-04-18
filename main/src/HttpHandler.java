import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface HttpHandler {
    void handle(Request request, OutputStream out) throws IOException;
}
