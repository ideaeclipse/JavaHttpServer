package ideaeclipse.JavaHttpServer.httpServer.responses;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Takes a response code and a attachment type
 * and generates a valid http header
 *
 * @author Ideaeclipse
 */
public class Header {
    private final static Map<String, String> MIME_MAP = new HashMap<>();
    private final static Map<Integer, String> codes = new HashMap<>();
    private final StringBuilder builder = new StringBuilder();

    static {
        MIME_MAP.put("appcache", "text/cache-manifest");
        MIME_MAP.put("css", "text/css");
        MIME_MAP.put("gif", "image/gif");
        MIME_MAP.put("html", "text/html");
        MIME_MAP.put("js", "application/javascript");
        MIME_MAP.put("json", "application/json");
        MIME_MAP.put("jpg", "image/jpeg");
        MIME_MAP.put("ico", "image/x-icon");
        MIME_MAP.put("jpeg", "image/jpeg");
        MIME_MAP.put("mp4", "video/mp4");
        MIME_MAP.put("pdf", "application/pdf");
        MIME_MAP.put("png", "image/png");
        MIME_MAP.put("svg", "image/svg+xml");
        MIME_MAP.put("xlsm", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        MIME_MAP.put("xml", "application/xml");
        MIME_MAP.put("zip", "application/zip");
        MIME_MAP.put("md", "text/plain");
        MIME_MAP.put("txt", "text/plain");
        MIME_MAP.put("php", "text/plain");

        codes.put(200, "OK");
        codes.put(301, "Moved Permanently");
        codes.put(303, "See Other");
        codes.put(400, "Bad Request");
        codes.put(401, "Unauthorized");
        codes.put(403, "Forbidden");
        codes.put(404, "Not Found");
        codes.put(405, "Method Not Allowed");
    }

    /**
     * @param code Response code
     * @param type attachment file type
     */
    public Header(final int code, final String type) {
        if (codes.get(code) != null)
            builder.append("HTTP/1.1 ").append(code).append(" ").append(codes.get(code)).append("\n");
        else
            builder.append("HTTP/1.1 ").append(404).append(" ").append(codes.get(404)).append("\n");
        add("Server", "JavaHttpServer");
        add("Date", new Date().toString());
        add("Content-Type", MIME_MAP.get(type));
    }

    /**
     * Adds a key value pair to the header
     *
     * @param key key to add
     * @param value value to add
     */
    public void add(final String key, final String value) {
        builder.append(key).append(": ").append(value).append("\n");
    }

    /**
     * @return header to be written to socket
     */
    public String getHeader() {
        return String.valueOf(builder + "\n");
    }
}
