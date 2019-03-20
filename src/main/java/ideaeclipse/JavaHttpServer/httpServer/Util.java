package ideaeclipse.JavaHttpServer.httpServer;

import ideaeclipse.JavaHttpServer.httpServer.responses.objects.Header;
import ideaeclipse.JavaHttpServer.httpServer.responses.IHeader;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class Util {
    public static String getFileExtention(final File file) {
        return file.getName().split("\\.")[1];
    }

    public static void redirect(final Socket socket, final String redirectUrl) throws IOException {
        IHeader header = new Header(301, "html");
        header.add("Location", redirectUrl);
        socket.getOutputStream().write(header.getHeader().getBytes());
        socket.getOutputStream().flush();
        socket.getOutputStream().close();
    }
    public static void blank(final Socket socket) throws IOException {
        IHeader header = new Header(404, "html");
        socket.getOutputStream().write(header.getHeader().getBytes());
        socket.getOutputStream().flush();
        socket.getOutputStream().close();
    }
}
