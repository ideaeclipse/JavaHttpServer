package JavaHttpServer;

import JavaHttpServer.Html.Response;

import java.io.PrintWriter;

/**
 * Stores a printwriter and allows a user to send a page as a string
 *
 * @author Ideaeclipse
 * @see Response
 */
public class Writer {
    private final PrintWriter out;

    Writer(final PrintWriter out) {
        this.out = out;
    }

    public void sendPage(final Response page) {
        out.println(page.getResponse());
        out.flush();
        out.close();
    }
}
