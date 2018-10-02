package JavaHttpServer.Html;

import java.util.Date;

/**
 * This class allows a user to create a http response header using a response code
 *
 * @author Ideaeclipse
 * @see ResponseCodes
 */
public class Header {
    private String header = "";

    public Header(final ResponseCodes codes) {
        header += "HTTP/1.0 " + codes.name() + " " + codes.getString() + "\n";
        header += "Server: Java HTTP Server 1.0" + "\n";
        header += "Date: " + new Date() + "\n";
        header += "Content-Type: text/html" + "\n\n";
    }

    public String getHeader() {
        return header;
    }
}
