import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequestParser {
    private static final Pattern request = Pattern.compile("(GET|POST|PATCH|PUT|DELETE) (\\S+) HTTP/1\\.1");
    private static final Pattern header = Pattern.compile("([\\w-]+):\\s*(.+)$");

    public Request parse(BufferedReader reader) throws IOException {
        String line;
        String method = "";
        String path = "";
        HashMap<String, String> headers = new HashMap<>();
        String body = "";

        String firstLine =  reader.readLine();
        if (firstLine == null) return null;

        Matcher requestMatcher = request.matcher(firstLine);
        if (requestMatcher.find()) {
            method = requestMatcher.group(1);
            path = requestMatcher.group(2);
        }

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            requestMatcher = request.matcher(line);
            if (requestMatcher.find()) {
                method = requestMatcher.group(1);
                path = requestMatcher.group(2);
            }

            Matcher headerMatcher = header.matcher(line);
            if (headerMatcher.find()) {
                var headerKey = headerMatcher.group(1);
                var headerValue = headerMatcher.group(2);
                headers.put(headerKey, headerValue);
            }
        }

        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        if (contentLength > 0) {
            char[] bodyChar = new char[contentLength];
            int totalRead = 0;
            while (totalRead < contentLength) {
                int read = reader.read(bodyChar, totalRead, contentLength - totalRead);
                if (read == -1) break;
                totalRead += read;
            }
            body = new String(bodyChar);
        }

        return new Request(method, path, headers, body);
    }
}
