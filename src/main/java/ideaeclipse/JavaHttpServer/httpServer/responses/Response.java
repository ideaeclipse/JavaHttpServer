package ideaeclipse.JavaHttpServer.httpServer.responses;

import ideaeclipse.JavaHttpServer.httpServer.Util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

/**
 * Allows each endpoint to send a response
 *
 * @author Ideaeclipse
 */
public class Response {
    private final Socket socket;
    private final Map<String, File> privateData;

    /**
     * Creates a response object for each socket connection
     *
     * @param socket      socket object
     * @param privateData list of private data files
     */
    public Response(final Socket socket, final Map<String, File> privateData) {
        this.socket = socket;
        this.privateData = privateData;
    }

    /**
     * Sends a file as a response.
     * Works for html, css, pdf, png, ... {@link Header#MIME_MAP}
     *
     * @param code   response code
     * @param string path to file
     * @throws IOException if file can't be found
     */
    public void sendFile(final int code, final String string) throws IOException {
        File file = this.privateData.get(string);
        sendFile(code, file);
    }

    /**
     * Sends a file as a response.
     * Works for html, css, pdf, png, ... {@link Header#MIME_MAP}
     *
     * @param code response code
     * @param file file object
     * @throws IOException if file can't be found
     */
    public void sendFile(final int code, final File file) throws IOException {
        if (file != null) {
            Header header = new Header(code, Util.getFileExtention(file));
            header.add("Content-length", String.valueOf(file.length()));
            writeData(header, Files.readAllBytes(file.toPath()));
        }else{
            System.out.println("File is null");
        }
    }

    /**
     * Sends a json as a response
     *
     * @param code   response code
     * @param string string to send
     * @throws IOException if header can't be generated
     */
    public void sendJson(final int code, final String string) throws IOException {
        Header header = new Header(code, "json");
        header.add("Content-length", String.valueOf(string.getBytes().length));
        writeData(header, string.getBytes());
    }

    /**
     * Writes header and attachment to socket
     *
     * @param header header object
     * @param bytes  attachment as byte array
     * @throws IOException if data can't be written
     */
    private void writeData(final Header header, final byte[] bytes) throws IOException {
        this.socket.getOutputStream().write(header.getHeader().getBytes());

        this.socket.getOutputStream().flush();

        BufferedOutputStream dataOut = new BufferedOutputStream(this.socket.getOutputStream());
        dataOut.write(bytes, 0, bytes.length);
        dataOut.flush();

        this.socket.getOutputStream().close();
    }
}
