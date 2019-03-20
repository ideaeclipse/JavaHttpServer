package ideaeclipse.JavaHttpServer.httpServer.responses;

import ideaeclipse.JavaHttpServer.httpServer.responses.objects.Header;
import ideaeclipse.JavaHttpServer.httpServer.responses.objects.Response;

import java.net.Socket;
import java.util.Map;

/**
 * Used to generate a valid header. This class really isn't meant for users to interact with
 * if you want to generate a response see {@link Response#Response(Socket, Map)}
 *
 * @author Myles T
 * @see Header
 */
public interface IHeader {
    /**
     * This is used to add a specific value to the header before generating the
     * final header. This is used to say add Content-Length because you want to attach a file
     *
     * Example) Content-Length: 10
     * @param key   key for the entry i.e Content-Length
     * @param value value for the entry i.e 10
     */
    void add(final String key, final String value);

    /**
     * This is used to get the actual header that is properly spaces with a blank line
     * in-between the header and the content below it
     * @return header
     */
    String getHeader();
}
