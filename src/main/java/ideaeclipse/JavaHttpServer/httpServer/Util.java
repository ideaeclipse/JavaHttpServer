package ideaeclipse.JavaHttpServer.httpServer;

import ideaeclipse.JavaHttpServer.httpServer.responses.objects.Header;
import ideaeclipse.JavaHttpServer.httpServer.responses.IHeader;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is a static class to have methods that can be used anywhere
 * See method description for info on each specific function
 *
 * @author Myles T
 * @see ideaeclipse.JavaHttpServer.httpServer.objects.HttpServer
 */
public class Util {
    /**
     * This is used to get the extension ty
     *
     * @param file file to get the extension from
     * @return the file's extension
     */
    public static String getFileExtension(final File file) {
        return file.getName().split("\\.")[1];
    }

    /**
     * This can be used to redirect a user to another endpoint, this will open send a redirect header code 301
     * and will start a new connection if the user allows redirects. I.e programmatically you must enable redirects but
     * through a browser you will always be redirected
     *
     * @param socket socket generate by {@link ServerSocket#accept()}
     * @param redirectUrl the end point to redirect to see {@link ideaeclipse.JavaHttpServer.httpServer.objects.HttpServer.InputHandler} for logic
     * @throws IOException If there's an issue writing to the socket
     */
    public static void redirect(final Socket socket, final String redirectUrl) throws IOException {
        IHeader header = new Header(301, "html");
        header.add("Location", redirectUrl);
        socket.getOutputStream().write(header.getHeader().getBytes());
        socket.getOutputStream().flush();
        socket.getOutputStream().close();
    }

    /**
     * Returns a blank 404. This is only useful if you want to kill a non web based connection because the connection
     * won't hang on their end
     * @param socket socket generate by {@link ServerSocket#accept()}
     * @throws IOException If there's an issue writing to the socket
     */
    public static void blank(final Socket socket) throws IOException {
        IHeader header = new Header(404, "html");
        socket.getOutputStream().write(header.getHeader().getBytes());
        socket.getOutputStream().flush();
        socket.getOutputStream().close();
    }
}
