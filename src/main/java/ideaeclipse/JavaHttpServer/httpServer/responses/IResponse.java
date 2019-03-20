package ideaeclipse.JavaHttpServer.httpServer.responses;

import ideaeclipse.JavaHttpServer.httpServer.responses.objects.Response;

import java.io.File;
import java.io.IOException;

/**
 * This class is used to generate and send responses over the socket
 * This class is intended to be used by the user
 *
 * @author Myles T
 * @see Response
 */
public interface IResponse {
    /**
     * This class will send a file over the socket, the string is the name of a file located in the
     * private Data directory that you specified when starting the bot
     *
     * @param code   response code, i.e 200
     * @param string file name, i.e test.html
     * @throws IOException if the file can't be found a blank response is returned
     * @see ideaeclipse.JavaHttpServer.httpServer.objects.HttpServer.LoadWorkDirData
     */
    void sendFile(final int code, final String string) throws IOException;

    /**
     * This class will send a file over the socket that maybe is in another directory besides the private dir.
     *
     * @param code response code, i.e 200
     * @param file file object
     * @throws IOException if the file is null, a blank response is returned
     */
    void sendFile(final int code, final File file) throws IOException;

    /**
     * This class will send a raw json response over the socket
     *
     * @param code response code, i.e 404
     * @param string json string
     * @throws IOException if the socket is null or unresponsive
     */
    void sendJson(final int code, final String string) throws IOException;
}
